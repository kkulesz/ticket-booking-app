package domain

import zio.json._

final case class Room(id: String, rows: Int, columns: Int)

object Room {
  implicit val decoder: JsonDecoder[Room] = DeriveJsonDecoder.gen[Room]
  implicit val encoder: JsonEncoder[Room] = DeriveJsonEncoder.gen[Room]
}
