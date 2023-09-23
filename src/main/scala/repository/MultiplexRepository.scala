package repository

import java.util.UUID
import java.time.LocalDateTime
import zio.ZIO
import zio.Task

import domain._

trait MultiplexRepository {
  def getScreeningsFromTime(timestamp: LocalDateTime): Task[List[Screening]]
  def getScreeningById(id: UUID): Task[Option[Screening]]
  def getMoviesByIds(ids: List[UUID]): Task[List[Movie]]
  def getScreeningReservations(screeningId: UUID): Task[List[Reservation]]
  def getRoomById(id: String): Task[Option[Room]]
  def insertReservations(newReservations: List[Reservation]): Task[Unit]
}

object MultiplexRepository {
  def getScreeningsFromTime(timestamp: LocalDateTime): ZIO[MultiplexRepository, Throwable, List[Screening]] =
    ZIO.serviceWithZIO[MultiplexRepository](_.getScreeningsFromTime(timestamp))

  def getScreeningById(id: UUID): ZIO[MultiplexRepository, Throwable, Option[Screening]] =
    ZIO.serviceWithZIO[MultiplexRepository](_.getScreeningById(id))

  def getMoviesByIds(ids: List[UUID]): ZIO[MultiplexRepository, Throwable, List[Movie]] =
    ZIO.serviceWithZIO[MultiplexRepository](_.getMoviesByIds(ids))

  def getScreeningReservations(screeningId: UUID): ZIO[MultiplexRepository, Throwable, List[Reservation]] =
    ZIO.serviceWithZIO[MultiplexRepository](_.getScreeningReservations(screeningId))

  def getRoomById(id: String): ZIO[MultiplexRepository, Throwable, Option[Room]] =
    ZIO.serviceWithZIO[MultiplexRepository](_.getRoomById(id))

  def insertReservations(newReservations: List[Reservation]): ZIO[MultiplexRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[MultiplexRepository](_.insertReservations(newReservations))
}
