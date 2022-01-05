package application

import domain.bank_account.services.alarms.`less than X amount on bank account`
import org.apache.kafka.streams.scala.StreamsBuilder

object BankAccountAlarms {

  def apply()(implicit builder: StreamsBuilder) = {
    new `less than X amount on bank account`(
      1000,
      (key, bankAccount) =>
        println(
          s"Alarm, amount in bank account $key is less than ${bankAccount.amount}"
        )
    )
  }
}
