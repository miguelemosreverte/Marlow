package domain.bank_account

import domain.bank_account.BankEvent.BankEvent

object serialization {
  object json {

    import io.leonard.TraitFormat.{caseObjectFormat, traitFormat}
    import play.api.libs.json.Json.format
    import play.api.libs.json._

    implicit val user =
      format[domain.bank_account.entities.User]
    implicit val bankAccountId =
      format[domain.bank_account.entities.BankAccountId]

    implicit val bankCommandsFormat = {
      import domain.bank_account.BankCommands.atomic._
      traitFormat[BankCommands] <<
        format[AddOwner] <<
        format[RemoveOwner] <<
        format[MakeDeposit] <<
        format[MakeWithdraw]
    }

    implicit val bankEventsFormat = {
      import domain.bank_account.BankEvent.atomic._
      traitFormat[BankEvent] <<
        format[AddOwner] <<
        format[RemoveOwner] <<
        format[Deposit] <<
        format[Withdraw]
    }

    implicit val bankErrorsFormat = {
      import domain.bank_account.BankErrors._
      traitFormat[BankErrors] <<
        caseObjectFormat(BalanceMustBePositive) <<
        caseObjectFormat(OnlyOwnerOfAccountCanWithdraw) <<
        caseObjectFormat(AccountMustHaveAtleastOneOwner)
    }

    implicit val bankAccountFormat = format[BankAccount]

  }
}
