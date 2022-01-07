package domain.bank_account.services.rules.orchestration

import domain.bank_account.commands.BankCommands.orchestration.{
  TransferAllFunds => TransferAllFundsCommand
}
import domain.bank_account.errors.BankErrors
import domain.bank_account.events.BankEvent.orchestration.TransferAllFunds
import domain.bank_account.events.BankEvent.{atomic, orchestration}
import domain.bank_account.services.BankServices.BankRules
import domain.bank_account.state.BankAccount

object `transfer all funds`
    extends BankRules[orchestration.TransferAllFunds, TransferAllFundsCommand] {
  override def validator(
      context: BankAccount
  ): TransferAllFundsCommand => Either[BankErrors, Seq[TransferAllFunds]] = {
    case TransferAllFundsCommand(from, to) =>
      Right(
        Seq(
          TransferAllFunds(from, atomic.Withdraw(context.amount)),
          TransferAllFunds(to, atomic.Deposit(context.amount))
        )
      )
  }
}
