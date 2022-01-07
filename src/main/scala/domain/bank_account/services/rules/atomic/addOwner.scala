package domain.bank_account.services.rules.atomic

import domain.bank_account.commands.BankCommands.atomic.{
  AddOwner => AddOwnerCommand
}
import domain.bank_account.errors.BankErrors
import domain.bank_account.events.BankEvent.atomic
import domain.bank_account.events.BankEvent.atomic._
import domain.bank_account.services.BankServices.BankRules
import domain.bank_account.state.BankAccount

object addOwner extends BankRules[atomic.AddOwner, AddOwnerCommand] {
  override def validator(
      context: BankAccount
  ): AddOwnerCommand => Either[BankErrors, Seq[AddOwner]] = {
    case AddOwnerCommand(owner) =>
      Right(Seq apply AddOwner(owner))
  }
}
