package domain.bank_account

import domain.bank_account.commands.BankCommands
import domain.bank_account.errors.BankErrors
import domain.bank_account.events.BankEvent.BankEvent
import domain.bank_account.state.BankAccount

object serialization {
  object json {

    import io.leonard.TraitFormat.{caseObjectFormat, traitFormat}
    import play.api.libs.json.Json.format

    implicit val userFormat =
      format[entities.entities.User]
    implicit val bankAccountIdFormat =
      format[entities.entities.BankAccountId]

    implicit val bankCommandsFormat = {
      import domain.bank_account.commands.BankCommands.atomic._
      traitFormat[BankCommands] <<
        format[AddOwner] <<
        format[RemoveOwner] <<
        format[Deposit] <<
        format[MakeWithdraw]
    }

    implicit val bankEventsFormat = {
      import domain.bank_account.events.BankEvent.atomic._
      traitFormat[BankEvent] <<
        format[AddOwner] <<
        format[RemoveOwner] <<
        format[Deposit] <<
        format[Withdraw]
    }

    implicit val bankErrorsFormat = {
      import domain.bank_account.errors.BankErrors._
      traitFormat[BankErrors] <<
        caseObjectFormat(BalanceMustBePositive) <<
        caseObjectFormat(OnlyOwnerOfAccountCanWithdraw) <<
        caseObjectFormat(AccountMustHaveAtleastOneOwner)
    }

    implicit val bankAccountFormat = format[BankAccount]

  }
}
