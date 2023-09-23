package api.responseModel

import zio.json._

final case class DetailedScreeningResponse(roomId: String, freeSeats: List[(Int, Int)])

object DetailedScreeningResponse {
  implicit val decoder: JsonDecoder[DetailedScreeningResponse] = DeriveJsonDecoder.gen[DetailedScreeningResponse]
  implicit val encoder: JsonEncoder[DetailedScreeningResponse] = DeriveJsonEncoder.gen[DetailedScreeningResponse]
}
