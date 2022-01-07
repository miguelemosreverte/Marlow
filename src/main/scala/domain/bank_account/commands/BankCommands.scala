package domain.bank_account.commands

import domain.bank_account.entities.entities.{BankAccountId, User}

sealed trait BankCommands
object BankCommands {

  object atomic {
    sealed trait command extends BankCommands
    case class MakeWithdraw(who: User, amount: Int) extends command
    case class Deposit(amount: Int) extends command
    case class AddOwner(owner: User) extends command
    case class RemoveOwner(owner: User) extends command
  }

  object orchestration {
    sealed trait command extends BankCommands
    case class TransferBankAccountToOtherOwner(
        bankAccountId: BankAccountId,
        from: User,
        to: User
    ) extends command
    case class TransferAllFunds(from: BankAccountId, to: BankAccountId)
        extends command
  }
}
