package infrastructure.kafka.optimistic_concurrency_control

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}

trait OptimisticConcurrencyControl {
  def expectedVersion: Long
}
object OptimisticConcurrencyControl {
  def apply[
      Key,
      A <: OptimisticConcurrencyControl,
      B <: OptimisticConcurrencyControl
  ](topic: String, stream: KStream[Key, A], table: KTable[Key, B])(implicit
      serdeKey: Serde[Key],
      serdeA: Serde[A],
      serdeB: Serde[B]
  ): KStream[Key, A] = {

    import infrastructure.kafka.utils.KafkaStreamsUtils.KStreamEitherUtils
    val (retry, proceed) = stream
      .leftJoin(table) { (command, state) =>
        println("Reading from state")
        Option(state) match {
          case Some(state)
              if command.expectedVersion != state.expectedVersion =>
            Left apply command
          case _ => Right apply command
        }
      }
      .splitEither("optimisticConcurrencyControl")(
        "retry",
        "proceed"
      )

    retry.to(topic)
    proceed

  }

}
