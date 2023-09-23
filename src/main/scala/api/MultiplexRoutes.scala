package api

import zio._
import zio.http._
import zio.json._
import java.time.LocalDateTime
import repository.MultiplexRepository

object MultiplexRoutes {
  def apply(): Http[MultiplexRepository, Nothing, Request, Response] =
    Http.collectZIO[Request] {
      case req @ Method.GET -> Root / "movies" =>
        val response = for {
          time <- ZIO.fromOption(req.url.queryParams.get("time"))
          properTime = LocalDateTime.parse(time.asString)
          screenings <- MultiplexRepository.getScreeningsFromTime(properTime)
          response = Response.json(screenings.toJson)
        } yield response

        response.catchAll(_ => ZIO.succeed(Response.status(Status.BadRequest)))
        
      case req @ Method.POST -> Root / "movies" => ???
    }
}
