package infrastructure.kafka.utils

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.serialization.Serdes
import play.api.libs.json.{Format, JsError, JsSuccess, Json}

import java.nio.charset.StandardCharsets

object SerdeUtils {
  implicit def serde[A >: Null: Format]: Serde[A] = {
    val serializer = (a: A) => {
      println(s"Serializing $a")
      Json.prettyPrint(Json.toJson(a)).getBytes
    }
    val deserializer = (aAsBytes: Array[Byte]) => {
      val aAsString = new String(aAsBytes, StandardCharsets.UTF_8)
      println(
        s"Deserializing ${aAsString} ${Json.fromJson(Json.parse(aAsBytes))}"
      )

      Json.fromJson(Json.parse(aAsBytes)) match {
        case JsSuccess(value, path) =>
          println(
            s"There was an zero problem! the original message was: $aAsString the result is $value"
          )
          Option(value)
        case JsError(errors) =>
          println(
            s"There was an error converting the message $errors, the original message was: $aAsString"
          )
          Option.empty
      }
    }
    Serdes.fromFn[A](serializer, deserializer)
  }

}
