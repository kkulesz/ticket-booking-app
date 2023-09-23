package models.domainModel

import zio.json._

// For simplicity:
// - each room is rectangle
// - each room is supposed to have different name, no validation there
final case class Room(id: String, rows: Int, columns: Int)

object Room {
  implicit val decoder: JsonDecoder[Room] = DeriveJsonDecoder.gen[Room]
  implicit val encoder: JsonEncoder[Room] = DeriveJsonEncoder.gen[Room]
}

