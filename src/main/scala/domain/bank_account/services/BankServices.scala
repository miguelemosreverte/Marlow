package domain.bank_account.services

import domain.bank_account.commands.BankCommands
import domain.bank_account.entities.entities.BankAccountId
import domain.bank_account.errors.BankErrors
import domain.bank_account.events.BankEvent.BankEvent
import domain.bank_account.state.BankAccount
import domain.{Alarm, Rules}

trait BankServices

object BankServices {

  trait BankRules[Event <: BankEvent, Command <: BankCommands]
      extends Rules[Event, BankAccount, Command, BankErrors]

  trait BankAlarm extends Alarm[BankAccountId, BankAccount]

}
