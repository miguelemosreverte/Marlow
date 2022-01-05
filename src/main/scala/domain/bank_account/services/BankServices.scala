package domain.bank_account.services

import domain.{Alarm, Rules}
import domain.bank_account.commands.BankCommands
import domain.bank_account.errors.BankErrors
import domain.bank_account.events.BankEvent.BankEvent
import domain.bank_account.state.BankAccount

trait BankServices

object BankServices {

  trait BankRules[Event <: BankEvent, Command <: BankCommands]
      extends Rules[Event, BankAccount, Command, BankErrors]

  trait BankAlarm[State <: BankAccount] extends Alarm[State]

}
