package domain

import zio.json._
import java.util.UUID

final case class Reservation(
    screeningId: UUID,
    row: Int,
    column: Int,
    name: String,
    surname: String,
    ticketType: TicketType
)

object Reservation {
  implicit val decoder: JsonDecoder[Reservation] =
    DeriveJsonDecoder.gen[Reservation]
  implicit val encoder: JsonEncoder[Reservation] =
    DeriveJsonEncoder.gen[Reservation]
}
