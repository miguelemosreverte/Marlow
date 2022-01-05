package domain.bank_account.state

import domain.State
import domain.bank_account.entities.entities.User
import domain.bank_account.events.BankEvent.BankEvent
import domain.bank_account.events.BankEvent.atomic.{
  AddOwner,
  Deposit,
  RemoveOwner,
  Withdraw
}

case class BankAccount(
    amount: Int,
    owners: Set[User]
) extends State[BankEvent, BankAccount] {
  override def +(event: BankEvent): BankAccount = {
    (event match {
      case Withdraw(amount)   => copy(amount = this.amount - amount)
      case Deposit(amount)    => copy(amount = this.amount + amount)
      case RemoveOwner(owner) => copy(owners = this.owners - owner)
      case AddOwner(owner)    => copy(owners = this.owners + owner)
    })
  }
}
