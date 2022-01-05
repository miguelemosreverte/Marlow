package domain.bank_account.services

import domain.Rules
import domain.bank_account.{BankAccount, BankCommands, BankErrors}
import domain.bank_account.BankEvent.BankEvent

trait BankServices

object BankServices {

  trait BankRules[Event <: BankEvent]
      extends Rules[Event, BankAccount, BankCommands, BankErrors]

  trait Alarm[State] extends BankServices {
    def predicate(key: String, state: State): Boolean
    def perform(key: String, state: State): Unit
  }

}
