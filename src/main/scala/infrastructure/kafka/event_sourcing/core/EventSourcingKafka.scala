package infrastructure.kafka.event_sourcing.core

import domain.{Rules, State}
import infrastructure.kafka.optimistic_concurrency_control.OptimisticConcurrencyControl
import infrastructure.kafka.utils.KafkaStreamsUtils.{
  KStreamEitherUtils,
  KTableDefaultValue
}
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._

abstract class EventSourcingKafka[
    Key,
    Command,
    Event,
    S <: State[Event, S],
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
    eventsSerde: Serde[Seq[Event]],
    stateSerde: Serde[S],
    builder: StreamsBuilder
) {

  lazy val commandsTopic: String = s"${name}-commands"
  lazy val eventsTopic: String = s"${name}-events"
  lazy val snapshotsTopic: String = s"${name}-snapshots"

  def eventsStream: KStream[Key, Event] =
    builder.stream[Key, Event](eventsTopic)(
      Consumed
        .`with`[Key, Event]
        .withName(s"$eventsTopic.in")
    )

  def snapshots: KTable[Key, S] = eventsStream.groupByKey
    .aggregate(
      initialState,
      org.apache.kafka.streams.kstream.Named.as(s"$snapshotsTopic.aggregate")
    )((key, event, currentSnapshot) => currentSnapshot + event)(
      Materialized.as(s"$snapshotsTopic-store")
    )

  def commands: KStream[Key, Command] =
    builder.stream(commandsTopic)(
      Consumed
        .`with`[Key, Command]
        .withName(s"$commandsTopic.in")
    )

  def process = {

    val (rejectedCommands, events: KStream[Key, Seq[Event]]) =
      commands
        .leftJoinWithDefault(initialState)(snapshots) { (command, state) =>
          rules
            .validator(state)
            .apply(command)
        }(
          Joined
            .`with`[Key, Command, S]
            .withName(s"$commandsTopic-join-$snapshotsTopic")
        )
        .splitEither("commandValidation")(
          "rejectedCommands",
          "events"
        )

    events
      .flatMapValues(k => k, Named as s"$eventsTopic.flatten")
      .to(eventsTopic)(
        Produced.`with`[Key, Event].withName(s"$eventsTopic.out")
      )
    snapshots.toStream.to(snapshotsTopic)(
      Produced.`with`[Key, S].withName(s"$snapshotsTopic.out")
    )
  }

}
