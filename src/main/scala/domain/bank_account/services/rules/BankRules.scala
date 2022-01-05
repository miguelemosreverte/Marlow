package domain.bank_account.services.rules

import domain.bank_account.{BankAccount, BankCommands, BankErrors}
import domain.bank_account.BankEvent.BankEvent
import domain.bank_account.services.BankServices.BankRules

case object BankRules extends BankRules[BankEvent] {
  override def validator(
      context: BankAccount
  ): BankCommands => Either[BankErrors, BankEvent] = {
    case c: BankCommands.atomic.command =>
      atomic.AtomicRules.validator(context)(c)
  }
}
