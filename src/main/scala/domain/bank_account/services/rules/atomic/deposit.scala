package domain.bank_account.services.rules.atomic

import domain.bank_account.commands.BankCommands.atomic.{
  MakeDeposit => DepositCommand
}
import domain.bank_account.errors.BankErrors
import domain.bank_account.events.BankEvent.atomic
import domain.bank_account.events.BankEvent.atomic._
import domain.bank_account.services.BankServices.BankRules
import domain.bank_account.state.BankAccount

object deposit extends BankRules[atomic.Deposit, DepositCommand] {
  override def validator(
      context: BankAccount
  ): DepositCommand => Either[BankErrors, Deposit] = {
    case DepositCommand(amount) =>
      Right(Deposit(amount))

  }
}
