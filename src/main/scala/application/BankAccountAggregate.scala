package application

import domain.bank_account.BankEvent.BankEvent
import domain.bank_account._
import domain.bank_account.services.BankServices.BankRules
import domain.bank_account.services.rules.BankRules
import infrastructure.kafka.optimistic_concurrency_control.CQRSESKafkaSimple.CQRSESKafkaSimple
import org.apache.kafka.streams.scala.StreamsBuilder

object BankAccountAggregate {

  def apply()(implicit builder: StreamsBuilder) = {
    import domain.bank_account.serialization.json._
    import infrastructure.kafka.utils.SerdeUtils._
    new CQRSESKafkaSimple[
      String,
      BankCommands,
      BankEvent,
      BankAccount,
      BankErrors,
      BankRules[BankEvent]
    ]("BankAccount", BankAccount(0, Set.empty), BankRules)

  }
}
