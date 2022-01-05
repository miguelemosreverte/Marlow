name := "Marlow"

version := "0.1"

scalaVersion := "2.13.7"
libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "2.8.0",
  "org.apache.kafka" %% "kafka-streams-scala" % "2.8.0",
  "io.leonard" %% "play-json-traits" % "1.5.1",
  "com.github.scopt" %% "scopt" % "4.0.1"
)
