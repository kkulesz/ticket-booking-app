package repository

import java.time.LocalDateTime
import zio.ZIO
import zio.Task

import models.domainModel._

trait MultiplexRepository {
  def getScreeningsFromTime(timestamp: LocalDateTime): Task[List[Screening]]
}

object MultiplexRepository {
  def getScreeningsFromTime(timestamp: LocalDateTime): ZIO[MultiplexRepository, Throwable, List[Screening]] =
    ZIO.serviceWithZIO[MultiplexRepository](_.getScreeningsFromTime(timestamp))
}
