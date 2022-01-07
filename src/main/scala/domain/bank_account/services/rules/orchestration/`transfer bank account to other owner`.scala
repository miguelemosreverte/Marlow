package domain.bank_account.services.rules.orchestration

import domain.bank_account.commands.BankCommands.orchestration.{
  TransferBankAccountToOtherOwner => TransferBankAccountToOtherOwnerCommand
}
import domain.bank_account.errors.BankErrors
import domain.bank_account.events.BankEvent.orchestration.TransferBankAccountToOtherOwner
import domain.bank_account.events.BankEvent.{atomic, orchestration}
import domain.bank_account.services.BankServices.BankRules
import domain.bank_account.state.BankAccount

object `transfer bank account to other owner`
    extends BankRules[
      orchestration.TransferBankAccountToOtherOwner,
      TransferBankAccountToOtherOwnerCommand
    ] {
  override def validator(
      context: BankAccount
  ): TransferBankAccountToOtherOwnerCommand => Either[
    BankErrors,
    Seq[TransferBankAccountToOtherOwner]
  ] = { case TransferBankAccountToOtherOwnerCommand(id, from, to) =>
    Right(
      Seq(
        TransferBankAccountToOtherOwner(id, atomic.RemoveOwner(from)),
        TransferBankAccountToOtherOwner(id, atomic.AddOwner(to))
      )
    )

  }
}
