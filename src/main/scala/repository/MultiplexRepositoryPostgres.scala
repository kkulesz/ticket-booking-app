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
}

object MultiplexRepositoryPostgres {
  object Queries {
    def getScreeningsFromTime(
        timestamp: LocalDateTime
    )(implicit read: Read[ScreeningResponse]): Query0[ScreeningResponse] =
      sql"""SELECT 
                s.id, m.title, s.time, m.length 
            FROM screenings s
            JOIN movies m 
              ON s.movie_id = m.id
            WHERE s.time = ${timestamp} 
            ORDER BY 
              m.title ASC, s.time ASC;"""
        .query[(String, String, LocalDateTime, Int)]
        .map { case (id, title, time, length) =>
          ScreeningResponse.fromDbValues(id, title, time, length)
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
          val r = Reservation(
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
  }
  def layer: ZLayer[Any, Nothing, MultiplexRepositoryPostgres] =
    ZLayer.fromZIO(ZIO.succeed(new MultiplexRepositoryPostgres()))
}
