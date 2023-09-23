package repository

import domain._
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

  override def getMoviesByIds(ids: List[UUID]): Task[List[Movie]] =
    ZIO.succeed(
      movies.filter(m => ids.contains(m.id))
    )

  override def getScreeningById(id: UUID): Task[Option[Screening]] =
    ZIO.succeed(
      screenings.find(_.id == id)
    )

  override def getScreeningReservations(
      screeningId: UUID
  ): Task[List[Reservation]] =
    ZIO.succeed(
      reservations.filter(_.screeningId == screeningId)
    )

  override def getRoomById(id: String): Task[Option[Room]] =
    ZIO.succeed(
      rooms.find(_.id == id)
    )

   override def insertReservations(newReservations: List[Reservation]): Task[Unit] = 
    ZIO.succeed(
      this.reservations = this.reservations ++ newReservations //TODO comment
    )

  private val movies: List[Movie] = List(
    Movie(UUID.fromString("67f4b3d9-2451-422c-8366-ff5ca12ec72a"), "Szybcy i wściekli", 90),
    Movie(UUID.fromString("67f4b3d9-2451-422c-8366-ff5ca12ec72b"), "Piraci z karaibów", 120),
    Movie(UUID.fromString("67f4b3d9-2451-422c-8366-ff5ca12ec72c"), "Incepcja", 150)
  )
  private val rooms: List[Room] = List(
    Room("pokój nr 1", 10, 10),
    Room("pokój nr 2", 10, 20),
    Room("pokój nr 3", 5, 10)
  )
  private val screenings: List[Screening] = List(
    Screening(
      UUID.fromString("99f4b3d9-2451-422c-8366-ff5ca12ec72a"),
      movies(0).id,
      rooms(0).id,
      LocalDateTime.parse("2023-09-22T12:00:00")
    ),
    Screening(
      UUID.fromString("99f4b3d9-2451-422c-8366-ff5ca12ec72b"),
      movies(1).id,
      rooms(1).id,
      LocalDateTime.parse("2023-09-22T12:30:00")
    )
  )

  // I know we should never use 'var', because it's not functional and it's just not the way you write in Scala.
  // Normally this should be some kind of database, of course
  private var reservations: List[Reservation] = List()

}

object MultiplexRepositoryInMemory {
  def layer: ZLayer[Any, Nothing, MultiplexRepositoryInMemory] =
    ZLayer.fromZIO(ZIO.succeed(new MultiplexRepositoryInMemory()))
}
