package api.requestModel

import zio.json._

import models.domainModel.TicketType

final case class SeatReservation(seat: (Int, Int), ticketType: TicketType)


object SeatReservation {
  implicit val decoder: JsonDecoder[SeatReservation] = DeriveJsonDecoder.gen[SeatReservation]
  implicit val encoder: JsonEncoder[SeatReservation] = DeriveJsonEncoder.gen[SeatReservation]
}
