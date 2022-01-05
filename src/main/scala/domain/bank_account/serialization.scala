package domain.bank_account

import domain.bank_account.BankEvent.AddOwner

object serialization {
  object json {
    import io.leonard.TraitFormat.{caseObjectFormat, traitFormat}
    import play.api.libs.json.Json.format
    import play.api.libs.json._

    implicit val bankCommandsFormat = traitFormat[BankCommands] <<
      format[BankCommands.AddOwner] <<
      format[BankCommands.RemoveOwner] <<
      format[BankCommands.MakeDeposit] <<
      format[BankCommands.MakeWithdraw]

    implicit val bankEventsFormat = traitFormat[BankEvent] <<
      format[BankEvent.AddOwner] <<
      format[BankEvent.RemoveOwner] <<
      format[BankEvent.Deposit] <<
      format[BankEvent.Withdraw]

    implicit val bankErrorsFormat = traitFormat[BankErrors] <<
      caseObjectFormat(BankErrors.BalanceMustBePositive) <<
      caseObjectFormat(BankErrors.OnlyOwnerOfAccountCanWithdraw) <<
      caseObjectFormat(BankErrors.AccountMustHaveAtleastOneOwner)

    implicit val bankAccountFormat = format[BankAccount]

    val doggyJson = bankEventsFormat.writes(AddOwner("1")).toString

    val animal1: BankEvent = bankEventsFormat.reads(Json.parse(doggyJson)).get
    animal1 == AddOwner("1")

    bankEventsFormat
      .writes(AddOwner("1"))
      .toString() == """{"s":"owner","Cat"}"""
  }
}
