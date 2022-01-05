package domain.bank_account

import domain.bank_account.entities.User

sealed trait BankCommands
object BankCommands {

  object atomic {
    sealed trait command extends BankCommands
    case class MakeWithdraw(who: User, amount: Int) extends command
    case class MakeDeposit(amount: Int) extends command
    case class AddOwner(owner: User) extends command
    case class RemoveOwner(owner: User) extends command
  }
}
