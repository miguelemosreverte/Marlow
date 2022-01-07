package application

object Main {

  def main(args: Array[String]): Unit = {
    Menu.apply(args) match {
      case Some(options) =>
        infrastructure.kafka.Setup.apply { implicit builder =>
          println("Starting BankAccountAggregate")
          if (options.bankAccountAggregate)
            BankAccountAggregate()
          if (options.bankAccountAlarm)
            BankAccountAlarms()
        }

      case _ =>
        println("""
                  |To see the available options use --help 
                  |""".stripMargin)
    }

  }
}
