package api.responseModel

import zio.json._
import java.time.LocalDateTime

final case class ReservationSummaryResponse(amountToPay: BigDecimal, expirationTime: LocalDateTime)

object ReservationSummaryResponse {
  implicit val decoder: JsonDecoder[ReservationSummaryResponse] = DeriveJsonDecoder.gen[ReservationSummaryResponse]
  implicit val encoder: JsonEncoder[ReservationSummaryResponse] = DeriveJsonEncoder.gen[ReservationSummaryResponse]
}
