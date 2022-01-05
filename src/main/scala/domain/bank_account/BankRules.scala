package domain.bank_account

import domain.Rules

sealed trait BankRules
    extends Rules[BankEvent, BankAccount, BankCommands, BankErrors]
case object BankRules extends BankRules {
  import BankCommands._
  import BankErrors._
  def validator(
      context: BankAccount
  ): BankCommands => Either[BankErrors, BankEvent] = {
    case MakeWithdraw(who, amount) =>
      context.owners contains who match {
        case false =>
          Left(OnlyOwnerOfAccountCanWithdraw)
        case true =>
          if (context.amount < amount)
            Left(BalanceMustBePositive)
          else
            Right(BankEvent.Withdraw(amount))
      }
    case MakeDeposit(amount) =>
      Right(BankEvent.Deposit(amount))
    case RemoveOwner(owner) =>
      if (context.owners.size == 1)
        Left(AccountMustHaveAtleastOneOwner)
      else
        Right(BankEvent.RemoveOwner(owner))
    case AddOwner(owner) =>
      Right(BankEvent.AddOwner(owner))

  }
}
