import application.BankAccountAggregate
import domain.bank_account.commands.BankCommands
import domain.bank_account.commands.BankCommands.atomic.{AddOwner, Deposit}
import domain.bank_account.entities.entities.{BankAccountId, User}
import domain.bank_account.events.BankEvent.BankEvent
import io.github.embeddedkafka.Codecs._
import io.github.embeddedkafka.EmbeddedKafkaConfig
import io.github.embeddedkafka.streams.EmbeddedKafkaStreams._
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.{Consumed, KStream, Produced}
import org.scalatest.Assertion
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{Format, Json}

import scala.concurrent.duration._
import scala.jdk.CollectionConverters._
import domain.bank_account.serialization.json._
import domain.bank_account.state.BankAccount
import infrastructure.kafka.utils.SerdeUtils._
import infrastructure.kafka.optimistic_concurrency_control.HasOptimisticConcurrencyControl.{
  State => EnrichedState
}
import infrastructure.kafka.optimistic_concurrency_control.HasOptimisticConcurrencyControl.State.format
import scala.language.implicitConversions

class ExampleKafkaStreamsSpec
    extends AnyWordSpec
    with Matchers
    with Eventually {
  val (inTopic, outTopic) = ("in", "out")

  val stringSerde: Serde[String] = Serdes.String()

  "A Kafka streams test" should {

    "be easy to run with streams on arbitrary available ports" in {
      val userDefinedConfig: EmbeddedKafkaConfig =
        EmbeddedKafkaConfig(kafkaPort = 0, zooKeeperPort = 0)

      implicit val streamBuilder =
        new org.apache.kafka.streams.scala.StreamsBuilder(new StreamsBuilder)

      BankAccountAggregate()

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
      object Dataset {
        val accountA = BankAccountId("A")
      }

      import Dataset._
      import Topics._
      runStreamsOnFoundPort(userDefinedConfig)(
        Topics(),
        streamBuilder.build()
      ) { implicit config =>
        implicit def toJson[A: Format](a: A): String =
          Json.prettyPrint(Json.toJson(a))

        implicit def fromJson[A: Format](a: String): A =
          Json.fromJson[A](Json.parse(a)).get

        val setup = { bankAccount: BankAccountId => owner: User =>
          (Seq(AddOwner(owner)) ++ (1 to 2).map(e => Deposit(1))).foreach {
            command: BankCommands =>
              publishToKafka[String, String](
                bankAccountCommands,
                toJson(bankAccount),
                toJson(command)
              )
          }
        }

        setup(BankAccountId("A"))(User("A"))
        setup(BankAccountId("B"))(User("B"))

        implicit val patienceConfig: PatienceConfig =
          PatienceConfig(150.seconds, 15.seconds)

        eventually {

          val firstTwoMessages
              : Seq[(BankAccountId, EnrichedState[BankEvent, BankAccount])] =
            consumeNumberKeyedMessagesFrom[String, String](
              bankAccountSnapshots,
              2
            ).map { a =>
              fromJson[BankAccountId](a._1) -> fromJson[
                EnrichedState[BankEvent, BankAccount]
              ](a._2)
            }

          println("HERE>")
          firstTwoMessages foreach println
          println("<HERE")

          firstTwoMessages should be(
            Seq(
              BankAccountId("A") ->
                EnrichedState[BankEvent, BankAccount](
                  BankAccount(2, Set(User("A"))),
                  expectedVersion = 4
                ),
              BankAccountId("B") ->
                EnrichedState[BankEvent, BankAccount](
                  BankAccount(2, Set(User("B"))),
                  expectedVersion = 4
                )
            )
          )
        }

      }
    }

  }
}
