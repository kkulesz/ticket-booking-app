package api

import zio._
import zio.http._
import zio.json._
import models.TestObject
import java.time.LocalDateTime

object TestRoutes {
  def apply(): Http[Any, Nothing, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> Root / "test"  => ZIO.succeed(Response.ok)
      case Method.GET -> Root / "test2" => ZIO.succeed(Response.ok)
      case req @ Method.POST -> Root / "test" =>
        val response = for {
          stringBody <- req.body.asString
          responseString = stringBody.fromJson[TestObject] match {
            case Left(e) =>
              val jsonMsg = s"""{"error": "$e"}"""
              println(jsonMsg)
              jsonMsg
            case Right(r) =>
              r.toJson
          }
          jsonResponse = Response.json(responseString)
        } yield jsonResponse
        response.catchAll(_ => ZIO.succeed(Response.status(Status.BadRequest)))
    }
}
