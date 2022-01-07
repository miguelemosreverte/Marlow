package application

import domain.bank_account.entities.entities.BankAccountId
import domain.bank_account.serialization.json._
import domain.bank_account.services.alarms.`less than X amount on bank account`
import domain.bank_account.state.BankAccount
import infrastructure.kafka.readside_processor.FilteredForeach
import infrastructure.kafka.utils.SerdeUtils.serde
import org.apache.kafka.streams.scala.StreamsBuilder

object BankAccountAlarms {

  def apply()(implicit builder: StreamsBuilder) = {
    val alarm = new `less than X amount on bank account`(
      1000,
      (key, bankAccount) =>
        println(
          s"Alarm, amount in bank account $key is less than ${bankAccount.amount}"
        )
    )

    new FilteredForeach[BankAccountId, BankAccount](
      "BankAccount-snapshots",
      alarm.predicate,
      alarm.perform,
      "minimumAmountAlarm"
    ).process
  }
}
