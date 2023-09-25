package repository

import zio._
import zio.{Task, ZLayer, ZIO}
import java.util.UUID
import java.time.LocalDateTime
// import zio.interop.catz._

import doobie.implicits._
import doobie.refined.implicits._
import doobie.util.transactor.Transactor
import doobie.{Query0, Update0}
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.implicits.javasql._
import doobie.implicits.javatime._

import domain._
import domain.viewModel._
import doobie.util.Read

class MultiplexRepositoryPostgres extends MultiplexRepository {

  override def getScreeningsFromTime(
      timestamp: LocalDateTime
  ): Task[List[ScreeningResponse]] = ???
  // Queries
  //   .getScreeningsFromTime(timestamp)
  //   .to[List]
  //   .transact(xa)

  override def getScreeningById(id: UUID): Task[Option[Screening]] = ???
  // Queries
  // .getScreeningById(id)
  // .option
  // .transact(xa)

  override def getScreeningReservations(
      screeningId: UUID
  ): Task[List[Reservation]] = ???
  // Queries
  // .getScreeningReservations(screeningId)
  // .to[List]
  // .transact(xa)

  override def getRoomById(id: String): Task[Option[Room]] = ???
  // Queries
  // .getRoomById(id)
  // .option
  // .transact(xa)

  override def insertReservations(
      newReservations: List[Reservation]
  ): Task[Unit] = ???
  // Queries
  // .insertReservations(newReservations)
  // .run
  // .transact(xa)
}

object MultiplexRepositoryPostgres {
  object Queries {
    def getScreeningsFromTime(
        timestamp: LocalDateTime
    )(implicit read: Read[ScreeningResponse]): Query0[ScreeningResponse] =
      sql""" SELECT 
                s.id, m.title, s.time, m.length 
              FROM screenings s
              WHERE s.time = ${timestamp}
              LEFT JOIN 
                movies m ON s.movieId = m.id 
              ORDER BY 
                m.title ASC, s.time ASC;
        """.query[ScreeningResponse]
  }

  def getScreeningById(
      id: UUID
  )(implicit read: Read[Screening]): Query0[Screening] =
    sql"""  SELECT 
              s.id, s.movie_id, room_id, time
            FROM screenings s
            WHERE s.id = ${id};
    """.query[Screening]

  def getScreeningReservations(
      screeningId: UUID
  )(implicit read: Read[Reservation]): Query0[Reservation] =
    sql"""  SELECT
              r.screening_id, r.row, r.column, r.name, r.surname, r.ticket_type
            FROM reservations
            WHERE r.screening_id = ${screeningId}
    """.query[Reservation]

  def getRoomById(
      id: String
  )(implicit read: Read[Room]): Query0[Room] =
    sql"""  SELECT 
              m.id, m.title. m.length
            FROM movies m
            WHERE m.id = ${id};
    """.query[Room]

  def insertReservations(
      nr: List[Reservation]
  ): Update0 = ???
  //   sql"""  INSERT INTO reservations
  //             (screening_id, row, column, name, surname, ticket_type)
  //           VALUES
  //             ()
  // """.updateMany(nr) TODO: figure out how to do it

  def layer: ZLayer[Any, Nothing, MultiplexRepositoryPostgres] =
    ZLayer.fromZIO(ZIO.succeed(new MultiplexRepositoryPostgres()))
}
