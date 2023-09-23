package models.domainModel

import zio.json._
import java.time.LocalDateTime
import java.util.UUID

final case class Screening(id: UUID, movieId: UUID, time: LocalDateTime)

object Screening {
  implicit val decoder: JsonDecoder[Screening] = DeriveJsonDecoder.gen[Screening]
  implicit val encoder: JsonEncoder[Screening] = DeriveJsonEncoder.gen[Screening]
}
