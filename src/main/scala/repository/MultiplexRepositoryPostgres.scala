package repository

import zio.{Task, ZLayer, ZIO}
import java.util.UUID
import java.time.LocalDateTime
import zio.interop.catz._

import doobie.implicits._
import doobie.refined.implicits._
import doobie.util.transactor.Transactor
import doobie.{Query0, Update0}
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.implicits.javasql._
import doobie.implicits.javatime._
import doobie.util.ExecutionContexts
import doobie.util.Read
import cats.effect.Blocker
import cats._
import cats.effect._
import cats.implicits._
import doobie._

import domain._
import domain.viewModel._
import utils.Config

import repository.MultiplexRepositoryPostgres.Queries

class MultiplexRepositoryPostgres extends MultiplexRepository {

  val xa: Transactor[Task] = Transactor
    .fromDriverManager[Task](
      driver = "org.postgresql.Driver",
      url = "jdbc:postgresql://localhost:5434/postgres",
      user = "postgres",
      pass = "postgres"
    )

  override def getScreeningsFromTime(
      timestamp: LocalDateTime
  ): Task[List[ScreeningResponse]] =
    Queries
      .getScreeningsFromTime(timestamp)
      .to[List]
      .transact(xa)

  override def getScreeningById(id: UUID): Task[Option[Screening]] =
    Queries
      .getScreeningById(id)
      .option
      .transact(xa)

  override def getScreeningReservations(
      screeningId: UUID
  ): Task[List[Reservation]] =
    Queries
      .getScreeningReservations(screeningId)
      .to[List]
      .transact(xa)

  override def getRoomById(id: String): Task[Option[Room]] =
    Queries
      .getRoomById(id)
      .option
      .transact(xa)

  override def insertReservations(
      newReservations: List[Reservation]
  ): Task[Unit] =
    Queries
      .insertReservations()
      .updateMany(newReservations)
      .transact(xa)
      .map(_ => ())

  override def getVoucherById(id: UUID): Task[Option[Voucher]] =
    Queries
      .getVoucherById(id)
      .option
      .transact(xa)

  override def updateVoucher(voucher: Voucher): Task[Unit] =
    Queries
      .updateVoucher(voucher)
      .run
      .transact(xa)
      .map(_ => ())
}

object MultiplexRepositoryPostgres {
  object Queries {
    def getScreeningsFromTime(
        timestamp: LocalDateTime
    )(implicit read: Read[ScreeningResponse]): Query0[ScreeningResponse] = {
      val margin = Config.ScreeningTimeMarginInMinutes
      sql"""SELECT 
                s.id, m.title, s.time, m.length 
            FROM screenings s
            JOIN movies m 
              ON s.movie_id = m.id
            WHERE 
              s.time >= ${timestamp.minusMinutes(
          margin
        )} AND s.time <=${timestamp.plusMinutes(margin)} 
            ORDER BY 
              m.title ASC, s.time ASC;"""
        .query[(String, String, LocalDateTime, Int)]
        .map { case (id, title, time, length) =>
          ScreeningResponse.fromDbValues(id, title, time, length)
        }
    }

    def getScreeningById(
        id: UUID
    )(implicit read: Read[Screening]): Query0[Screening] =
      sql"""SELECT 
              s.id, s.movie_id, room_id, time
            FROM screenings s
            WHERE s.id = ${id.toString};"""
        .query[(String, String, String, LocalDateTime)]
        .map { case (id, mId, rId, time) =>
          Screening(
            UUID.fromString(id),
            UUID.fromString(mId),
            rId,
            time
          )
        }

    def getScreeningReservations(
        screeningId: UUID
    )(implicit read: Read[Reservation]): Query0[Reservation] =
      sql"""SELECT
              r.screening_id, r.row, r.col, r.name, r.surname, r.ticket_type
            FROM reservations r
            WHERE r.screening_id = ${screeningId.toString};"""
        .query[(String, Int, Int, String, String, String)]
        .map { case (id, row, col, name, surname, ticketType) =>
          Reservation(
            UUID.fromString(id),
            row,
            col,
            name,
            surname,
            TicketType.fromString(ticketType)
          )
        }

    def getRoomById(
        id: String
    )(implicit read: Read[Room]): Query0[Room] =
      sql"""SELECT 
              r.id, r.rows, r.cols
            FROM rooms r
            WHERE r.id = ${id};"""
        .query[(String, Int, Int)]
        .map { case (id, rows, cols) =>
          Room(id, rows, cols)
        }

    def insertReservations() = {
      val stmt = """
      INSERT INTO reservations
        (screening_id, row, col, name, surname, ticket_type)
      VALUES
        (?, ?, ?, ?, ?, ?)"""

      Update[Reservation](stmt)
    }

    def getVoucherById(id: UUID)(implicit
        read: Read[Voucher]
    ): Query0[Voucher] =
      sql"""SELECT 
              v.id, v.is_used
            FROM vouchers v
            WHERE v.id = ${id.toString};"""
        .query[(String, Boolean)]
        .map { case (id, isUsed) =>
          Voucher(UUID.fromString(id), isUsed)
        }

    def updateVoucher(voucher: Voucher): Update0 =
      sql"""UPDATE vouchers
            SET is_used = ${voucher.isUsed}
            WHERE id = ${voucher.id.toString}
      """.update
  }
  def layer: ZLayer[Any, Nothing, MultiplexRepositoryPostgres] =
    ZLayer.fromZIO(ZIO.succeed(new MultiplexRepositoryPostgres()))
}
