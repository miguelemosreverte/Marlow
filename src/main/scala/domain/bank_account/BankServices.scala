package domain.bank_account

import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.serialization.Serdes._

trait BankServices
object BankServices {
  trait Alarm[State] extends BankServices {
    def predicate(key: String, state: State): Boolean
    def perform(key: String, state: State): Unit
  }
  object Alarm {
    class LessThanXAmountInBankAccount(
        amount: Int,
        givenPerform: (String, BankAccount) => Unit
    )(implicit
        builder: StreamsBuilder
    ) extends Alarm[BankAccount] {

      override def predicate(key: String, state: BankAccount): Boolean =
        state.amount < amount
      override def perform(key: String, state: BankAccount): Unit =
        givenPerform(key, state)

      import domain.bank_account.serialization.json._
      import infrastructure.kafka.utils.SerdeUtils._

      builder
        .stream[String, BankAccount]("BankAccount-snapshots")
        .filter(predicate)
        .foreach(perform)
    }
  }
}
