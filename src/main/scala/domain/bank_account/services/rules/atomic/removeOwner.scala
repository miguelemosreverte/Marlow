package domain.bank_account.services.rules.atomic

import domain.bank_account.commands.BankCommands.atomic.{
  RemoveOwner => RemoveOwnerCommand
}
import domain.bank_account.errors.BankErrors
import domain.bank_account.errors.BankErrors.AccountMustHaveAtleastOneOwner
import domain.bank_account.events.BankEvent.atomic
import domain.bank_account.events.BankEvent.atomic._
import domain.bank_account.services.BankServices.BankRules
import domain.bank_account.state.BankAccount

object removeOwner extends BankRules[atomic.RemoveOwner, RemoveOwnerCommand] {
  override def validator(
      context: BankAccount
  ): RemoveOwnerCommand => Either[BankErrors, Seq[RemoveOwner]] = {
    case RemoveOwnerCommand(owner) =>
      if (context.owners.size == 1)
        Left(AccountMustHaveAtleastOneOwner)
      else
        Right(Seq apply RemoveOwner(owner))
  }
}
