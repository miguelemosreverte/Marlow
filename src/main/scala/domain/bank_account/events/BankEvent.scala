package domain.bank_account.events

import domain.bank_account.entities.entities.{BankAccountId, User}

object BankEvent {
  sealed trait BankEvent

  object atomic {
    sealed trait events extends BankEvent

    sealed trait TransferAmount extends events

    sealed trait TransferOwner extends events

    case class Withdraw(amount: Int) extends TransferAmount

    case class Deposit(amount: Int) extends TransferAmount

    case class AddOwner(owner: User) extends TransferOwner
    case class RemoveOwner(owner: User) extends TransferOwner
  }

  object orchestration {
    sealed trait events extends BankEvent
    case class TransferBankAccountToOtherOwner(
        id: BankAccountId,
        action: atomic.TransferOwner
    ) extends events
    case class TransferAllFunds(
        id: BankAccountId,
        action: atomic.TransferAmount
    ) extends events
  }
}
