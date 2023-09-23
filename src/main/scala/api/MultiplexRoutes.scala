package api

import zio._
import zio.http._
import zio.json._
import java.time.LocalDateTime
import java.util.UUID

import repository.MultiplexRepository
import api.requestModel.ReservationRequest

object MultiplexRoutes {
  def apply(): Http[
    MultiplexHandler with MultiplexRepository,
    Nothing,
    Request,
    Response
  ] =
    Http.collectZIO[Request] {
      case req @ Method.GET -> Root / "movies" =>
        val response = for {
          time <- ZIO.fromOption(req.url.queryParams.get("time"))
          properTime = LocalDateTime.parse(time.asString)
          screenings <- MultiplexHandler.getScreeningsFromTime(properTime)
        } yield Response.json(screenings.toJson)

        response.catchAll(_ => ZIO.succeed(Response.status(Status.BadRequest)))

      case req @ Method.GET -> Root / "screenings" =>
        val response = for {
          screeningId <- ZIO.fromOption(req.url.queryParams.get("id"))
          properId = UUID.fromString(screeningId.asString)
          detailedScreening <- MultiplexHandler.getDetailedScreening(properId)
        } yield Response.json(detailedScreening.toJson)

        response.catchAll(_ => ZIO.succeed(Response.status(Status.BadRequest)))

      case req @ Method.POST -> Root / "screenings" =>
        val response = for {
          stringBody <- req.body.asString
          request <- ZIO.fromEither(stringBody.fromJson[ReservationRequest])
          reservationSummary <- MultiplexHandler.handleReservationRequest(
            request
          )
        } yield Response.json(reservationSummary.toJson)

        response.catchAll(_ => ZIO.succeed(Response.status(Status.BadRequest)))
    }
}
