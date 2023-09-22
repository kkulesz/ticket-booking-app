package domain

import java.time.LocalDateTime
import zio.json.JsonDecoder
import zio.json.DeriveJsonDecoder
import zio.json.JsonEncoder
import zio.json.DeriveJsonEncoder

final case class TestObject(int: Int, string: String, dateTime: LocalDateTime)

object TestObject {
  implicit val decoder: JsonDecoder[TestObject] = DeriveJsonDecoder.gen[TestObject]
  implicit val encoder: JsonEncoder[TestObject] = DeriveJsonEncoder.gen[TestObject]
}
