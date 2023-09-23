package api.requestModel

import zio.json._

final case class ReservationRequest(name: String, surname: String, seats: List[SeatReservation])

object ReservationRequest {
  implicit val decoder: JsonDecoder[ReservationRequest] = DeriveJsonDecoder.gen[ReservationRequest]
  implicit val encoder: JsonEncoder[ReservationRequest] = DeriveJsonEncoder.gen[ReservationRequest]
}

