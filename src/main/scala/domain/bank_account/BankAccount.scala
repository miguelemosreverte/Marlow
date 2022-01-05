package domain.bank_account

import domain.State
import domain.bank_account.BankEvent.BankEvent
import domain.bank_account.BankEvent.atomic._
import domain.bank_account.entities.User

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
