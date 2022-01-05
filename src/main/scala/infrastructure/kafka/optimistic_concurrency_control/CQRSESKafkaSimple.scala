package infrastructure.kafka.optimistic_concurrency_control

import domain.{Rules, State}
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}
import org.apache.kafka.streams.scala.serialization.Serdes._
import play.api.libs.json.Format

object CQRSESKafkaSimple {
  /*
  object HasOptimisticConcurrencyControl {

    case class Command[A](
        payload: A,
        expectedVersion: Long
    ) extends OptimisticConcurrencyControl
    case class State[
        E,
        S <: domain.State[E, S]
    ](
        state: S,
        expectedVersion: Long
    ) extends domain.State[E, State[E, S]]
        with OptimisticConcurrencyControl {
      override def +(event: E): State[E, S] = {
        copy(
          expectedVersion = this.expectedVersion + 1,
          state = state + event
        )
      }
    }
    object State {
      implicit def helloEncoder[E: Encoder, S <: domain.State[E, S]: Encoder]
          : Encoder[State[E, S]] = new Encoder[State[E, S]] {
        override def apply(a: State[E, S]): Json =
          Json.obj(
            ("state", a.state.asJson),
            ("expectedVersion", Json.fromLong(a.expectedVersion))
          )
      }

      implicit def decodeFoo[E: Decoder, S <: domain.State[E, S]: Decoder]
          : Decoder[State[E, S]] = new Decoder[State[E, S]] {
        final def apply(c: HCursor): Decoder.Result[State[E, S]] =
          for {
            state <- c.downField("state").as[S]
            expectedVersion <- c.downField("expectedVersion").as[Long]
          } yield {
            State(state, expectedVersion)
          }
      }
    }
  }
   */
  class CQRSESKafkaSimple[
      Key,
      Command: Format,
      Event: Format,
      S <: State[Event, S]: Format,
      Error,
      R <: Rules[Event, S, Command, Error]
  ](
      name: String,
      initialState: S,
      rules: R
  )(implicit
      keySerde: Serde[Key],
      commandSerde: Serde[Command],
      eventSerde: Serde[Event],
      stateSerde: Serde[S],
      builder: StreamsBuilder
  ) {

    import HasOptimisticConcurrencyControl.{
      Command => EnrichedCommand,
      State => EnrichedState
    }

    val commandsTopic: String = s"${name}-commands"
    val eventsTopic: String = s"${name}-events"
    val snapshotsTopic: String = s"${name}-snapshots"

    println(s"Reading from $commandsTopic")
    val commandStream: KStream[Key, Command] =
      builder.stream[Key, Command](commandsTopic)

    val s: KTable[Key, Long] = commandStream.groupByKey.count()
    val a: KStream[Key, EnrichedCommand[Command]] =
      commandStream.join(s)((c, l) => EnrichedCommand(c, l))

    import EnrichedCommand.format
    import EnrichedState.format
    import infrastructure.kafka.utils.SerdeUtils.serde

    val zippedWithIndexTopic = s"$commandsTopic-zippedWithIndex"
    a.to(zippedWithIndexTopic)

    new CQRSESKafka[
      Key,
      EnrichedCommand[Command],
      Event,
      EnrichedState[Event, S],
      Error,
      Rules[Event, EnrichedState[Event, S], EnrichedCommand[Command], Error]
    ](
      name,
      EnrichedState(initialState, 0),
      context =>
        command => {
          rules.validator(context.state)(command.payload)
        }
    ) {
      override lazy val commandsTopic: String = zippedWithIndexTopic
    }

  }

}
