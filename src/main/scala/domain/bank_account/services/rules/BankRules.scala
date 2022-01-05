package domain.bank_account.services.rules

import domain.bank_account.commands.BankCommands
import domain.bank_account.commands.BankCommands.{atomic, orchestration}
import domain.bank_account.errors.BankErrors
import domain.bank_account.events.BankEvent.BankEvent
import domain.bank_account.services.BankServices.BankRules
import domain.bank_account.services.rules.atomic._
import domain.bank_account.services.rules.orchestration._
import domain.bank_account.state.BankAccount

case object BankRules extends BankRules[BankEvent, BankCommands] {
  override def validator(
      context: BankAccount
  ): BankCommands => Either[BankErrors, BankEvent] = {
    case c: BankCommands.atomic.command =>
      c match {
        case c: atomic.MakeWithdraw =>
          withdraw.validator(context)(c)
        case c: atomic.MakeDeposit =>
          deposit.validator(context)(c)
        case c: atomic.AddOwner =>
          addOwner.validator(context)(c)
        case c: atomic.RemoveOwner =>
          removeOwner.validator(context)(c)
      }
    case c: BankCommands.orchestration.command =>
      c match {
        case c: orchestration.TransferBankAccountToOtherOwner =>
          `transfer bank account to other owner`.validator(context)(c)
        case c: orchestration.TransferAllFunds =>
          `transfer all funds`.validator(context)(c)

      }
  }
}
