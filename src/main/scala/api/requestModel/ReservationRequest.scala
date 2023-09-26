package api.requestModel

import zio.json._
import java.util.UUID

import domain.Voucher

final case class ReservationRequest(
    name: String,
    surname: String,
    screeningId: UUID,
    seats: List[SeatReservation],
    voucher: Option[Voucher]
)

object ReservationRequest {
  implicit val decoder: JsonDecoder[ReservationRequest] =
    DeriveJsonDecoder.gen[ReservationRequest]
  implicit val encoder: JsonEncoder[ReservationRequest] =
    DeriveJsonEncoder.gen[ReservationRequest]
}
