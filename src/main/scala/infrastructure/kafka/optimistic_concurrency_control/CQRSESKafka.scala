package infrastructure.kafka.optimistic_concurrency_control

import domain.{Rules, State}
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}

class CQRSESKafka[
    Key,
    Command <: OptimisticConcurrencyControl,
    Event,
    S <: State[Event, S] with OptimisticConcurrencyControl,
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

  lazy val commandsTopic: String = s"${name}-commands"
  lazy val eventsTopic: String = s"${name}-events"
  lazy val snapshotsTopic: String = s"${name}-snapshots"

  val eventsStream: KStream[Key, Event] =
    builder.stream[Key, Event](eventsTopic)

  val snapshots: KTable[Key, S] = eventsStream.groupByKey
    .aggregate(initialState)((key, event, currentSnapshot) =>
      currentSnapshot + event
    )

  val commands: KStream[Key, Command] =
    OptimisticConcurrencyControl(
      commandsTopic,
      builder.stream(commandsTopic),
      snapshots
    )

  import infrastructure.kafka.utils.KafkaStreamsUtils.{
    KStreamEitherUtils,
    KTableDefaultValue
  }

  val (rejectedCommands, events) =
    commands
      .leftJoinWithDefault(initialState)(snapshots) { (command, state) =>
        rules
          .validator(state)
          .apply(command)
      }
      .splitEither("commandValidation")(
        "rejectedCommands",
        "events"
      )

  events.to(eventsTopic)
  snapshots.toStream.to(snapshotsTopic)

}
