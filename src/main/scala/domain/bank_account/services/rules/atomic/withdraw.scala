package domain.bank_account.services.rules.atomic

import domain.bank_account.commands.BankCommands.atomic.{
  MakeWithdraw => WithdrawCommand
}
import domain.bank_account.errors.BankErrors
import domain.bank_account.errors.BankErrors.{
  BalanceMustBePositive,
  OnlyOwnerOfAccountCanWithdraw
}
import domain.bank_account.events.BankEvent.atomic
import domain.bank_account.events.BankEvent.atomic._
import domain.bank_account.services.BankServices.BankRules
import domain.bank_account.state.BankAccount

object withdraw extends BankRules[atomic.Withdraw, WithdrawCommand] {
  override def validator(
      context: BankAccount
  ): WithdrawCommand => Either[BankErrors, Withdraw] = {
    case WithdrawCommand(who, amount) =>
      context.owners contains who match {
        case false =>
          Left(OnlyOwnerOfAccountCanWithdraw)
        case true =>
          if (context.amount < amount)
            Left(BalanceMustBePositive)
          else
            Right(Withdraw(amount))
      }
  }
}
