package repository

import java.time.LocalDateTime
import zio.ZLayer
import zio.ZIO
import zio.Task
import java.util.UUID

import domain._
import domain.viewModel._
import utils.Config

class MultiplexRepositoryInMemory extends MultiplexRepository {
  override def getScreeningsFromTime(
      timestamp: LocalDateTime
  ): Task[List[ScreeningResponse]] = {
    val margin = Config.MinimumPeriodBeforeBookingInMinutes
    val scrs = screenings.filter(scr =>
      scr.time.isAfter(timestamp.minusMinutes(margin)) &&
        scr.time.isBefore(timestamp.plusMinutes(margin))
    )

    val movieIds = scrs.map(_.movieId)
    val screeningsWithMovies = scrs
      .flatMap(s =>
        movies
          .filter(_.id == s.movieId)
          .map(m => (s, m))
      )
      // sorting in alphabetical order, then from earliest
      .sortWith { case ((s1, m1), (s2, m2)) =>
        if (m1.title == m2.title)
          s1.time.isBefore(s2.time)
        else
          m1.title < m2.title
      }

    val viewModel = screeningsWithMovies.map { case (s, m) =>
      ScreeningResponse.fromDomain(s, m)
    }

    ZIO.succeed(viewModel)
  }

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

  override def insertReservations(
      newReservations: List[Reservation]
  ): Task[Unit] =
    ZIO.succeed(
      this.reservations = this.reservations ++ newReservations
    )

  override def getVoucherById(id: UUID): Task[Option[Voucher]] =
    ZIO.succeed(
      vouchers.find(_.id == id)
    )

  override def updateVoucher(updated: Voucher): Task[Unit] =
    ZIO.succeed(
      vouchers.map(v =>
        if (v.id == updated.id) updated
        else v
      )
    )

  private val movies: List[Movie] = List(
    Movie(
      UUID.fromString("67f4b3d9-2451-422c-8366-ff5ca12ec72a"),
      "Szybcy i wściekli",
      90
    ),
    Movie(
      UUID.fromString("67f4b3d9-2451-422c-8366-ff5ca12ec72b"),
      "Piraci z karaibów",
      120
    ),
    Movie(
      UUID.fromString("67f4b3d9-2451-422c-8366-ff5ca12ec72c"),
      "Incepcja",
      150
    )
  )
  private val rooms: List[Room] = List(
    Room("pokój nr 1", 2, 2),
    Room("pokój nr 2", 3, 1),
    Room("pokój nr 3", 4, 3)
  )
  private val screenings: List[Screening] = List(
    // 1st room screenings
    Screening(
      UUID.fromString("99f4b3d9-2451-422c-8366-ff5ca12ec72a"),
      movies(0).id,
      rooms(0).id,
      LocalDateTime.parse("2023-09-22T12:00:00")
    ),
    Screening(
      UUID.fromString("99f4b3d9-2451-422c-8366-ff5ca12ec72b"),
      movies(1).id,
      rooms(0).id,
      LocalDateTime.parse("2023-09-22T12:30:00")
    ),
    // 2nd room screenings
    Screening(
      UUID.fromString("99f4b3d9-2451-422c-8366-ff5ca12ec72c"),
      movies(0).id,
      rooms(1).id,
      LocalDateTime.parse("2023-09-22T12:30:00")
    ),
    Screening(
      UUID.fromString("99f4b3d9-2451-422c-8366-ff5ca12ec72d"),
      movies(1).id,
      rooms(1).id,
      LocalDateTime.parse("2023-09-22T12:35:00")
    ),
    // 3rd room screenings
    Screening(
      UUID.fromString("99f4b3d9-2451-422c-8366-ff5ca12ec72e"),
      movies(1).id,
      rooms(2).id,
      LocalDateTime.parse("2023-09-22T12:30:00")
    ),
    Screening(
      UUID.fromString("99f4b3d9-2451-422c-8366-ff5ca12ec72f"),
      movies(2).id,
      rooms(2).id,
      LocalDateTime.parse("2023-09-22T12:35:00")
    )
  )

  // I know we should never use 'var', because it's not functional and it's just not the way you write in Scala.
  // Normally this should be some kind of database, of course
  private var reservations: List[Reservation] = List()

  private var vouchers: List[Voucher] = List(
    Voucher(UUID.fromString("00000000-2451-422c-8366-ff5ca12ec72a"), false),
    Voucher(UUID.fromString("00000000-2451-422c-8366-ff5ca12ec72b"), true)
  )

}

object MultiplexRepositoryInMemory {
  def layer: ZLayer[Any, Nothing, MultiplexRepositoryInMemory] =
    ZLayer.fromZIO(ZIO.succeed(new MultiplexRepositoryInMemory()))
}
