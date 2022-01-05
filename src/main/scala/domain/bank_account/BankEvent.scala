package domain.bank_account

import domain.bank_account.entities._

object BankEvent {
  sealed trait BankEvent

  object atomic {
    sealed trait events extends BankEvent
    case class Withdraw(amount: Int) extends events
    case class Deposit(amount: Int) extends events
    case class AddOwner(owner: User) extends events
    case class RemoveOwner(owner: User) extends events
  }

  object orchestration {
    sealed trait events extends BankEvent
    case class TransferBankAccountToOtherOwner(from: User, to: User) extends events
    case class TransferAllFunds(from: BankAccountId, to: BankAccountId) extends events
  }
}
