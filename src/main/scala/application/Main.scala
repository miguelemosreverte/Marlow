package application

import scopt.OParser

object Main {

  def main(args: Array[String]): Unit = {
    infrastructure.kafka.Setup.apply { implicit builder =>
      Menu.apply(args) match {
        case Some(options) =>
          if (options.bankAccountAggregate)
            BankAccountAggregate()
          if (options.bankAccountAlarm)
            BankAccountAlarms()
        case _ =>
          println("""
              |To see the available options use --help 
              |""".stripMargin)
      }

    }

  }
}
