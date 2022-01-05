package domain.bank_account.services.rules.atomic

import domain.Rules
import domain.bank_account.{BankAccount, BankCommands, BankErrors}
import domain.bank_account.BankEvent.BankEvent
import domain.bank_account.services.BankServices.BankRules

import domain.bank_account.BankEvent.atomic._
import domain.bank_account.BankCommands.atomic.{
  AddOwner => AddOwnerCommand,
  RemoveOwner => RemoveOwnerCommand,
  MakeWithdraw => WithdrawCommand,
  MakeDeposit => DepositCommand
}

case object AtomicRules extends BankRules[events] {
  import domain.bank_account.BankErrors._
  def validator(
      context: BankAccount
  ): BankCommands => Either[BankErrors, events] = {
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
    case DepositCommand(amount) =>
      Right(Deposit(amount))
    case RemoveOwnerCommand(owner) =>
      if (context.owners.size == 1)
        Left(AccountMustHaveAtleastOneOwner)
      else
        Right(RemoveOwner(owner))
    case AddOwnerCommand(owner) =>
      Right(AddOwner(owner))

  }
}
