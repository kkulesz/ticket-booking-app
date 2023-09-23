package api

import java.time.LocalDateTime
import zio.ZIO
import zio.Task
import zio.ZLayer
import java.util.UUID

import api.responseModel._
import repository.MultiplexRepository
import api.requestModel.ReservationRequest
import domain.Reservation

trait MultiplexHandler {
  def getScreeningsFromTime(
      timestamp: LocalDateTime
  ): ZIO[MultiplexRepository, Throwable, List[ScreeningResponse]]

  def getDetailedScreening(
      id: UUID
  ): ZIO[MultiplexRepository, Throwable, DetailedScreeningResponse]

  def handleReservationRequest(
      request: ReservationRequest
  ): ZIO[MultiplexRepository, Throwable, ReservationSummaryResponse]
}

object MultiplexHandler {
  def getScreeningsFromTime(
      timestamp: LocalDateTime
  ): ZIO[MultiplexHandler with MultiplexRepository, Throwable, List[ScreeningResponse]] =
    ZIO.serviceWithZIO[MultiplexHandler](_.getScreeningsFromTime(timestamp))

  def getDetailedScreening(
      id: UUID
  ): ZIO[MultiplexHandler with MultiplexRepository, Throwable, DetailedScreeningResponse] =
    ZIO.serviceWithZIO[MultiplexHandler](_.getDetailedScreening(id))

  def handleReservationRequest(
      request: ReservationRequest
  ): ZIO[MultiplexHandler with MultiplexRepository, Throwable, ReservationSummaryResponse] =
    ZIO.serviceWithZIO[MultiplexHandler](_.handleReservationRequest(request))
}

class MultiplexHandlerBasic extends MultiplexHandler {

  override def getScreeningsFromTime(
      timestamp: LocalDateTime
  ): ZIO[MultiplexRepository, Throwable, List[ScreeningResponse]] =
    for {
      // I am aware of the terrible performance in normal scale
      screenings <- MultiplexRepository.getScreeningsFromTime(timestamp)
      movieIds = screenings.map(_.movieId)
      movies <- MultiplexRepository.getMoviesByIds(movieIds)

      screeningsWithMovies = screenings.flatMap(s =>
        movies
          .filter(_.id == s.movieId)
          .map(m => (s, m))
      )

      // TODO: sort by (title, screening time)

      response = screeningsWithMovies.map { case (s, m) =>
        ScreeningResponse.fromDomain(s, m)
      }
    } yield response

  def getDetailedScreening(
      id: UUID
  ): ZIO[MultiplexRepository, Throwable, DetailedScreeningResponse] =
    for {
      screeningOpt <- MultiplexRepository.getScreeningById(id)
      screening <- ZIO
        .fromOption(screeningOpt)
        .mapError(_ => new RuntimeException("No such screening"))

      reservations <- MultiplexRepository.getScreeningReservations(screening.id)
      roomOpt <- MultiplexRepository.getRoomById(screening.roomId)

      // it should be some kind of internal server error, since its problem in data state
      room <- ZIO
        .fromOption(roomOpt)
        .mapError(_ => new RuntimeException("No such room"))

    } yield DetailedScreeningResponse.fromDomain(room, reservations)

  def handleReservationRequest(
      request: ReservationRequest
  ): ZIO[MultiplexRepository, Throwable, ReservationSummaryResponse] = for{
      _ <- validateName(request)

      screeningOpt <- MultiplexRepository.getScreeningById(request.screeningId)
      screening <- ZIO.fromOption(screeningOpt).mapError(_ => new RuntimeException("TODO"))
      reservationsSoFar <- MultiplexRepository.getScreeningReservations(screening.id)
      
      _ <- validateReservations(request, reservationsSoFar)
      newReservations = request.seats.map(
        sr => 
          Reservation(
            request.screeningId, 
            sr.seat._1, 
            sr.seat._2, 
            request.name, 
            request.surname, 
            sr.ticketType
            )
        )
      _ <- MultiplexRepository.insertReservations(newReservations)
      expirationDate = screening.time.minusMinutes(15) // reservation until 15 minutes before screening, TODO
      amountToPay = request.seats.map(_.ticketType.price).sum

  } yield ReservationSummaryResponse(amountToPay, expirationDate)


  private def validateName(
    request: ReservationRequest
  ): ZIO[MultiplexRepository, Throwable, Unit] = {
    // TODO
    // ZIO.fail(new RuntimeException("blad"))
    ZIO.unit
  }

  private def validateReservations(
    request: ReservationRequest,
    reservationsSoFar: List[Reservation]
  ): ZIO[MultiplexRepository, Throwable, Unit] = {
    // TODO
    ZIO.unit
  }

}

object MultiplexHandlerBasic {
  def layer: ZLayer[Any, Nothing, MultiplexHandlerBasic] =
    ZLayer.fromZIO(ZIO.succeed(new MultiplexHandlerBasic()))
}
