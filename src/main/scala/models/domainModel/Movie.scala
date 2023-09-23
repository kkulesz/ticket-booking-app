package models.domainModel

import zio.json._
import java.util.UUID

final case class Movie(id: UUID, title: String, length: Int)

object Movie {
  implicit val decoder: JsonDecoder[Movie] = DeriveJsonDecoder.gen[Movie]
  implicit val encoder: JsonEncoder[Movie] = DeriveJsonEncoder.gen[Movie]
}
