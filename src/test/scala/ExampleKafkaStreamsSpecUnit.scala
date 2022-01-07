import application.{BankAccountAggregate, BankAccountAlarms, Main}
import domain.bank_account.commands.BankCommands
import domain.bank_account.commands.BankCommands.atomic.{AddOwner, Deposit}
import domain.bank_account.entities.entities.{BankAccountId, User}
import domain.bank_account.events.BankEvent.BankEvent
import domain.bank_account.serialization.json._
import domain.bank_account.state.BankAccount
import infrastructure.kafka.optimistic_concurrency_control.HasOptimisticConcurrencyControl.State.format
import infrastructure.kafka.optimistic_concurrency_control.HasOptimisticConcurrencyControl.{
  State => EnrichedState
}
import io.github.embeddedkafka.Codecs._
import io.github.embeddedkafka.EmbeddedKafkaConfig
import io.github.embeddedkafka.streams.EmbeddedKafkaStreams._
import org.apache.kafka.common.serialization.{
  Deserializer,
  Serde,
  Serdes,
  Serializer
}
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.StreamsBuilder
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{Format, Json}

import java.nio.charset.StandardCharsets
import java.util
import java.util.Properties
import scala.concurrent.duration._
import scala.language.implicitConversions

object JsonI {
  implicit def toJson[A: Format](a: A): String =
    Json.prettyPrint(Json.toJson(a))

  implicit def fromJson[A: Format](a: String): A =
    Json.fromJson[A](Json.parse(a)).get

  case class JsonSerializer[A: Format]()
      extends Serializer[A]
      with Deserializer[A] { s: Serializer[A] with Deserializer[A] =>
    override def serialize(topic: String, data: A): Array[Byte] =
      toJson[A](data).getBytes

    override def deserialize(topic: String, data: Array[Byte]): A = fromJson(
      new String(data, StandardCharsets.UTF_8)
    )

    override def configure(
        configs: util.Map[String, _],
        isKey: Boolean
    ): Unit = {
      super[Serializer].configure(configs, isKey)
      super[Deserializer].configure(configs, isKey)
    }

  }

}
import JsonI._

class ExampleKafkaStreamsSpecUnit
    extends AnyWordSpec
    with Matchers
    with Eventually
    with BeforeAndAfterAll {
  val (inTopic, outTopic) = ("in", "out")

  val stringSerde: Serde[String] = Serdes.String()

  "A Kafka streams test" should {

    "be easy to run with streams on arbitrary available ports" in {

      implicit val builder = new StreamsBuilder

      println("Starting BankAccountAggregate")
      BankAccountAggregate()
      BankAccountAlarms()
      val topology = builder.build()
      import org.apache.kafka.streams.TopologyTestDriver

      implicit val kafkaProps: Properties = {
        val props = new Properties
        //props.put(StreamsConfig.APPLICATION_ID_CONFIG, "orders-application")
        //props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        //props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "exactly_once")
        props.put(
          StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
          "org.apache.kafka.streams.errors.LogAndContinueExceptionHandler"
        )
        props.put(
          StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
          org.apache.kafka.streams.scala.serialization.Serdes.stringSerde.getClass
        )
        props.put(
          StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
          org.apache.kafka.streams.scala.serialization.Serdes.stringSerde.getClass
        )
        props
      }
      val testDriver = new TopologyTestDriver(topology, kafkaProps)
      println(topology.describe())

      val inputTopic = testDriver.createInputTopic(
        "BankAccount-commands",
        JsonSerializer[BankAccountId](),
        JsonSerializer[BankCommands]()
      )
      val outputTopic = testDriver.createOutputTopic(
        "BankAccount-snapshot",
        JsonSerializer[BankAccountId](),
        JsonSerializer[BankCommands]()
      )

      def setup = { account: BankAccountId => owner: User =>
        (Seq(AddOwner(owner)) ++ (1 to 200).map(e => Deposit(1)))
          .foreach(inputTopic.pipeInput(account, _))
      }

      setup(BankAccountId("A"))(User("A"))
      setup(BankAccountId("B"))(User("B"))

      object Topics {
        val bankAccountCommands = "BankAccount-commands"
        val bankAccountCommandsZippedWithIndex =
          "BankAccount-commands-zippedWithIndex"
        val bankAccountEvents = "BankAccount-events"
        val bankAccountSnapshots = "BankAccount-snapshots"

        def apply() = Seq(
          bankAccountCommands,
          bankAccountEvents,
          bankAccountCommandsZippedWithIndex,
          bankAccountSnapshots
        )
      }

      import Topics._

      implicit val patienceConfig: PatienceConfig =
        PatienceConfig(150.seconds, 15.seconds)

      eventually {
        import scala.jdk.CollectionConverters._

        println(topology.describe())

        testDriver
          .getKeyValueStore[BankAccountId, EnrichedState[
            BankEvent,
            BankAccount
          ]]("BankAccount-snapshots-store")
          .get(BankAccountId("A")) should be(
          EnrichedState[BankEvent, BankAccount](
            BankAccount(200, Set(User("A"))),
            expectedVersion = 202
          )
        )

      }

    }

  }
}
