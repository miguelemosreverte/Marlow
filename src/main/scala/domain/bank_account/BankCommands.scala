package domain.bank_account

sealed trait BankCommands
object BankCommands {
  case class MakeWithdraw(who: String, amount: Int) extends BankCommands
  case class MakeDeposit(amount: Int) extends BankCommands
  case class AddOwner(owner: String) extends BankCommands
  case class RemoveOwner(owner: String) extends BankCommands

}
