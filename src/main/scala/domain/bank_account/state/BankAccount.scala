package domain.bank_account.state

import domain.State
import domain.bank_account.entities.entities.User
import domain.bank_account.events.BankEvent.atomic.{
  AddOwner,
  Deposit,
  RemoveOwner,
  Withdraw
}
import domain.bank_account.events.BankEvent.{BankEvent, atomic, orchestration}

case class BankAccount(
    amount: Int,
    owners: Set[User]
) extends State[BankEvent, BankAccount] {
  override def +(event: BankEvent): BankAccount = {
    event match {
      case event: atomic.events =>
        event match {
          case Withdraw(amount)   => copy(amount = this.amount - amount)
          case Deposit(amount)    => copy(amount = this.amount + amount)
          case RemoveOwner(owner) => copy(owners = this.owners - owner)
          case AddOwner(owner)    => copy(owners = this.owners + owner)
        }
      case event: orchestration.events =>
        event match {
          case orchestration.TransferBankAccountToOtherOwner(id, atomic) =>
            this + atomic
          case orchestration.TransferAllFunds(id, atomic) =>
            this + atomic
        }
    }

  }
}
