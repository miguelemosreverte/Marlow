package domain.bank_account

object entities {
  sealed trait Entity
  case class User(name: String) extends Entity
  case class BankAccountId(id: String) extends Entity

}
