package domain.bank_account.services.alarms

import domain.bank_account.entities.entities.BankAccountId
import domain.bank_account.services.BankServices.BankAlarm
import domain.bank_account.state.BankAccount

class `less than X amount on bank account`(
    amount: Int,
    givenPerform: (BankAccountId, BankAccount) => Unit
) extends BankAlarm {

  override def predicate =
    (key, state) => state.amount < amount

  override def perform =
    (key, state) => givenPerform(key, state)

}
