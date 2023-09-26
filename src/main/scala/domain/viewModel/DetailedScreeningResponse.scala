package domain.viewModel

import zio.json._
import domain.{Room, Reservation}

final case class DetailedScreeningResponse(
    roomId: String,
    freeSeats: List[(Int, Int)]
)

object DetailedScreeningResponse {
  implicit val decoder: JsonDecoder[DetailedScreeningResponse] =
    DeriveJsonDecoder.gen[DetailedScreeningResponse]
  implicit val encoder: JsonEncoder[DetailedScreeningResponse] =
    DeriveJsonEncoder.gen[DetailedScreeningResponse]

  def fromDomain(
      room: Room,
      reservations: List[Reservation]
  ): DetailedScreeningResponse = {
    val allSeats = for {
      r <- 0 until room.rows
      c <- 0 until room.columns
    } yield (r, c)
    val takenSeats = reservations.map(r => (r.row, r.column))
    val freeSeats = allSeats.diff(takenSeats).toList

    DetailedScreeningResponse(room.id, freeSeats)
  }
}
