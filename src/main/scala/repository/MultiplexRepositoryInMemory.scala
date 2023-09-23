package repository

import models.domainModel._
import java.time.LocalDateTime
import zio.ZLayer
import zio.ZIO
import zio.Task
import java.util.UUID

class MultiplexRepositoryInMemory extends MultiplexRepository {
  override def getScreeningsFromTime(
      timestamp: LocalDateTime
  ): Task[List[Screening]] =
    // get screenings that start in (t-30min, t+30min) range
    ZIO.succeed(
      screenings.filter(scr =>
        scr.time.isAfter(timestamp.minusMinutes(30)) &&
          scr.time.isBefore(timestamp.plusMinutes(30))
      )
    )

  // I know we should never use 'var', because it's not functional and it's just not the way you write in Scala.
  // Normally this should be some kind of database, of course
  private val movies: List[Movie] = List(
    Movie(UUID.randomUUID, "Szybcy i wściekli", 90),
    Movie(UUID.randomUUID, "Piraci z karaibów", 120),
    Movie(UUID.randomUUID, "Incepcja", 150)
  )
  private val screenings: List[Screening] = List(
    Screening(UUID.randomUUID, movies(0).id, LocalDateTime.parse("2023-09-22T12:00:00")),
    Screening(UUID.randomUUID, movies(1).id, LocalDateTime.parse("2023-09-22T12:30:00"))
  )
  private val rooms: List[Room] = List(
    Room("pokój nr 1", 10, 10),
    Room("pokój nr 2", 10, 20),
    Room("pokój nr 3", 5, 10)
  )

}

object MultiplexRepositoryInMemory {
  def layer: ZLayer[Any, Nothing, MultiplexRepositoryInMemory] =
    ZLayer.fromZIO(ZIO.succeed(new MultiplexRepositoryInMemory()))
}
