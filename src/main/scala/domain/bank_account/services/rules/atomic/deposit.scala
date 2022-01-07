package domain.bank_account.services.rules.atomic

import domain.bank_account.commands.BankCommands.atomic.{
  Deposit => DepositCommand
}
import domain.bank_account.errors.BankErrors
import domain.bank_account.events.BankEvent.atomic
import domain.bank_account.events.BankEvent.atomic._
import domain.bank_account.services.BankServices.BankRules
import domain.bank_account.state.BankAccount

object deposit extends BankRules[atomic.Deposit, DepositCommand] {
  override def validator(
      context: BankAccount
  ): DepositCommand => Either[BankErrors, Seq[Deposit]] = {
    case DepositCommand(amount) =>
      Right(Seq apply Deposit(amount))

  }
}
