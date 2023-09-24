package domain.viewModel

import zio.json._
import java.time.LocalDateTime
import java.util.UUID

import domain.{Screening, Movie}

final case class ScreeningResponse(
    id: UUID,
    movieTitle: String,
    timeStart: LocalDateTime,
    timeEnd: LocalDateTime
)

object ScreeningResponse {
  implicit val decoder: JsonDecoder[ScreeningResponse] =
    DeriveJsonDecoder.gen[ScreeningResponse]
  implicit val encoder: JsonEncoder[ScreeningResponse] =
    DeriveJsonEncoder.gen[ScreeningResponse]

  def fromDomain(screening: Screening, movie: Movie): ScreeningResponse =
    ScreeningResponse(
      screening.id,
      movie.title,
      screening.time,
      screening.time.plusMinutes(movie.length)
    )
}
