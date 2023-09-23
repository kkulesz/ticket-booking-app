package api.responseModel

import zio.json._
import java.time.LocalDateTime
import java.util.UUID

final case class ScreeningResponse(id: UUID, movieTitle: UUID, timeStart: LocalDateTime, timeEnd: LocalDateTime)

object ScreeningResponse {
  implicit val decoder: JsonDecoder[ScreeningResponse] = DeriveJsonDecoder.gen[ScreeningResponse]
  implicit val encoder: JsonEncoder[ScreeningResponse] = DeriveJsonEncoder.gen[ScreeningResponse]
}
