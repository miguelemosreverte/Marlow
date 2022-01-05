package application

import scopt.OParser

object Menu {

  val builder = OParser.builder[Options]

  import scopt.OParser

  def apply(args: Array[String]) =
    OParser.parse(menu, args, Options())

  private def menu = {
    import builder._
    OParser.sequence(
      programName("scopt"),
      head("scopt", "4.x"),
      // option -f, --foo
      opt[Boolean]("bankAccountAggregate")
        .action((x, c) => c.copy(bankAccountAggregate = true))
        .text("Start the core stream processing"),
      opt[Boolean]("bankAccountAlarm")
        .action((x, c) => c.copy(bankAccountAlarm = true))
        .text("Start the alarm system"),
      help("help").text("""
        |Use
        | --bankAccountAlarm to start the alarm system
        | --bankAccountAggregate to start the core stream system
        |""".stripMargin)
    )
  }

}
