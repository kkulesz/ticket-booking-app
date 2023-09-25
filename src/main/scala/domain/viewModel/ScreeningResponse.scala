package domain.viewModel

import zio.json._
import java.time.LocalDateTime
import java.util.UUID

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.circe.refined._

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

  implicit val circeCodec: Codec[ScreeningResponse] = deriveCodec

  def fromDomain(screening: Screening, movie: Movie): ScreeningResponse =
    ScreeningResponse(
      screening.id,
      movie.title,
      screening.time,
      screening.time.plusMinutes(movie.length)
    )
}
