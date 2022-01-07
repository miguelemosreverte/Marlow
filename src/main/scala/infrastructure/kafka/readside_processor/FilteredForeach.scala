package infrastructure.kafka.readside_processor

import infrastructure.kafka.readside_processor.ReadsideProcessor.EndOfPipeline
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.Consumed

class FilteredForeach[Key, Value](
    topic: String,
    predicate: (Key, Value) => Boolean,
    perform: (Key, Value) => Unit,
    processorName: String
)(implicit
    builder: StreamsBuilder,
    keySerde: Serde[Key],
    valueSerde: Serde[Value]
) extends EndOfPipeline {

  def process =
    builder
      .stream[Key, Value](topic)(
        Consumed
          .`with`[Key, Value]
          .withName(s"$topic-readBy-$processorName")
      )
      .filter(predicate, Named.as(s"$topic-readBy-$processorName.filter"))
      .foreach(perform, Named.as(s"$topic-readBy-$processorName.foreach"))
}
