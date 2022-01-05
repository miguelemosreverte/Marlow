package application

import domain.bank_account.BankServices.Alarm
import org.apache.kafka.streams.scala.StreamsBuilder

object BankAccountAlarm {

  def apply()(implicit builder: StreamsBuilder) = {
    new Alarm.LessThanXAmountInBankAccount(
      1000,
      (key, bankAccount) =>
        println(
          s"Alarm, amount in bank account $key is less than ${bankAccount.amount}"
        )
    )
  }
}
