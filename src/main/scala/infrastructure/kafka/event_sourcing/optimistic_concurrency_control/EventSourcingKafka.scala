package infrastructure.kafka.event_sourcing.optimistic_concurrency_control

import domain.{Rules, State}
import infrastructure.kafka.event_sourcing.core.EventSourcingKafka
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

class EventSourcingKeafka[
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
    eventsSerde: Serde[Seq[Event]],
    stateSerde: Serde[S],
    builder: StreamsBuilder
) extends EventSourcingKafka[Key, Command, Event, S, Error, R](
      name,
      initialState,
      rules
    ) {

  override def commands: KStream[Key, Command] =
    OptimisticConcurrencyControl(
      commandsTopic,
      builder.stream(commandsTopic)(
        Consumed
          .`with`[Key, Command]
          .withName(s"$commandsTopic.in")
      ),
      snapshots
    )

}
