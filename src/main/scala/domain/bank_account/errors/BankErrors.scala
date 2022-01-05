package domain.bank_account.errors

// dealing with domain errors
sealed trait BankErrors
object BankErrors {
  case object OnlyOwnerOfAccountCanWithdraw extends BankErrors
  case object BalanceMustBePositive extends BankErrors
  case object AccountMustHaveAtleastOneOwner extends BankErrors
}
