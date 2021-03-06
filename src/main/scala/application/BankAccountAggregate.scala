package application

import domain.bank_account.commands.BankCommands
import domain.bank_account.entities.entities.BankAccountId
import domain.bank_account.errors.BankErrors
import domain.bank_account.events.BankEvent.BankEvent
import domain.bank_account.services.BankServices.BankRules
import domain.bank_account.services.rules.BankRules
import domain.bank_account.state.BankAccount
import org.apache.kafka.streams.scala.StreamsBuilder
import domain.bank_account.serialization.json._
import infrastructure.kafka.event_sourcing.EventSourcingKafka
import infrastructure.kafka.utils.SerdeUtils._

object BankAccountAggregate {

  def apply()(implicit builder: StreamsBuilder) = {
    new EventSourcingKafka[
      BankAccountId,
      BankCommands,
      BankEvent,
      BankAccount,
      BankErrors,
      BankRules[BankEvent, BankCommands]
    ]("BankAccount", BankAccount(0, Set.empty), BankRules).process

  }
}
