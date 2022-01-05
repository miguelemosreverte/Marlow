package domain.bank_account

sealed trait BankEvent
object BankEvent {

  case class Withdraw(amount: Int) extends BankEvent
  case class Deposit(amount: Int) extends BankEvent
  case class AddOwner(owner: String) extends BankEvent
  case class RemoveOwner(owner: String) extends BankEvent
}
