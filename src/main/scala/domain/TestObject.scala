package domain

import java.time.LocalDateTime
import zio.json._

final case class TestObject(int: Int, string: String, dateTime: LocalDateTime, enum: MyTestEnum)

object TestObject {
  implicit val decoder: JsonDecoder[TestObject] = DeriveJsonDecoder.gen[TestObject]
  implicit val encoder: JsonEncoder[TestObject] = DeriveJsonEncoder.gen[TestObject]
}
