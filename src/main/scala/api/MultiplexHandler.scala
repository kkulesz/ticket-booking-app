package api

import java.time.LocalDateTime
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit;
import zio.ZIO
import zio.Task
import zio.ZLayer
import java.util.UUID

import repository.MultiplexRepository
import api.requestModel.ReservationRequest
import domain._
import domain.viewModel._
import utils.Config

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
  ): ZIO[
    MultiplexHandler with MultiplexRepository,
    Throwable,
    DetailedScreeningResponse
  ] =
    ZIO.serviceWithZIO[MultiplexHandler](_.getDetailedScreening(id))

  def handleReservationRequest(
      request: ReservationRequest
  ): ZIO[
    MultiplexHandler with MultiplexRepository,
    Throwable,
    ReservationSummaryResponse
  ] =
    ZIO.serviceWithZIO[MultiplexHandler](_.handleReservationRequest(request))
}

class MultiplexHandlerBasic extends MultiplexHandler {

  override def getScreeningsFromTime(
      timestamp: LocalDateTime
  ): ZIO[MultiplexRepository, Throwable, List[ScreeningResponse]] =
    MultiplexRepository.getScreeningsFromTime(timestamp)

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
      room <- ZIO
        .fromOption(roomOpt)
        .mapError(_ => new RuntimeException("Internal server error: No such room"))

    } yield DetailedScreeningResponse.fromDomain(room, reservations)

  def handleReservationRequest(
      request: ReservationRequest
  ): ZIO[MultiplexRepository, Throwable, ReservationSummaryResponse] = for {
    _ <- validateName(request)

    screeningOpt <- MultiplexRepository.getScreeningById(request.screeningId)
    screening <- ZIO
      .fromOption(screeningOpt)
      .mapError(_ => new RuntimeException("No such screening!"))
    // _ <- validateReservationHour(screening) TODO: commenting out for demo reasons

    eitherFailOrVoucherOpt <- request.voucher match { 
      case None => ZIO.succeed(Right(None))
      case Some(v) => MultiplexRepository
          .getVoucherById(v.id)
          .map{ case vDbOpt =>
            vDbOpt match{
              case None => Left(new RuntimeException("Invalid voucher!"))
              case Some(vDb) => 
                if (vDb.isUsed) 
                  Left(new RuntimeException("Voucher is already used!"))
                else 
                  Right(Some(vDb))
            }
          }
    }
    voucherOpt <- ZIO.fromEither(eitherFailOrVoucherOpt)

    reservationsSoFar <- MultiplexRepository
      .getScreeningReservations(screening.id)

    roomOpt <- MultiplexRepository.getRoomById(screening.roomId)
    room <- ZIO
      .fromOption(roomOpt)
      .mapError(_ => new RuntimeException("Internal server error: No such room"))

    _ <- validateReservations(request, reservationsSoFar, room)
    newReservations = request.seats.map(sr =>
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
    _ <- voucherOpt match {
      case None     => ZIO.unit
      case Some(v)  => MultiplexRepository.updateVoucher(Voucher(v.id, true))
    }
    expirationDate = screening.time
      .minusMinutes(Config.ReservationPeriodBeforeScreeningInMinutes)
    amountToPay = calculateAmountToPay(request, screening, voucherOpt)

  } yield ReservationSummaryResponse(amountToPay, expirationDate)

  private def validateName(
      request: ReservationRequest
  ): ZIO[MultiplexRepository, Throwable, Unit] = {
    val fn = request.name
    val sn = request.surname

    // only business ruquirements met, nothing more
    if (fn.length < Config.NameMinLength || sn.length < Config.NameMinLength)
      ZIO.fail(new RuntimeException("Name and surname must have at least 3 characters!"))
    else if (!fn(0).isUpper || !sn(0).isUpper)
      ZIO.fail(new RuntimeException("Name and surname must start with capital letter!"))
    else if (sn.count(_ == '-') == 1 && (sn.takeRight(1) == "-" || !sn.split('-')(1)(0).isUpper)) // ugly, I know
      ZIO.fail(new RuntimeException("All parts of surname must start with capital letter!"))
    else if (sn.count(_ == '-') > 1)
      ZIO.fail(new RuntimeException("Surname may have two parts at most!"))
    else
      ZIO.unit
  }

  private def validateReservationHour(
    screening: Screening
  ): ZIO[MultiplexRepository, Throwable, Unit] = 
    if (
      LocalDateTime.now.until(screening.time, ChronoUnit.MINUTES ) < 
        Config.MinimumPeriodBeforeBookingInMinutes
    )
      ZIO.fail(new RuntimeException("It's too late to book seats!"))
    else
      ZIO.unit

  private def validateReservations(
      request: ReservationRequest,
      reservationsSoFar: List[Reservation],
      room: Room
  ): ZIO[MultiplexRepository, Throwable, Unit] = {
    val seatsToBeReserved = request.seats.map(_.seat)
    val reservedSeats = reservationsSoFar.map(r => (r.row, r.column))

    if (request.seats.length == 0)
      ZIO.fail(new RuntimeException("Reservation must apply to at least one seat!"))
    else if (reservedSeats.intersect(seatsToBeReserved).length > 0)
      ZIO.fail(new RuntimeException("Seats already occupied!"))
    else if (seatsToBeReserved.find { case (r, c) => (r < 0 || r > room.rows) || (c < 0 || c >= room.columns)}.isDefined)
      ZIO.fail(new RuntimeException("Seat number out of range!"))
    else if (room.columns > 2 && checkIfSingleSeatIsNotLeftBetween(seatsToBeReserved, reservedSeats, room))
      ZIO.fail(new RuntimeException("There cannot be a single place left over in a row between two already reserved places!"))
    else
      ZIO.unit
  }

  private def checkIfSingleSeatIsNotLeftBetween(
      seatsToBeReserved: List[(Int, Int)],
      reservedSeats: List[(Int, Int)],
      room: Room
  ): Boolean = {
    val allReservedSeats = seatsToBeReserved ++ reservedSeats
    val groupedByRow = allReservedSeats
      .groupBy(_._1)
      .map { case (r, l) =>
        (r , l.map (_._2))
      }
      .toList

    /*
    the idea is:
      for each row that has at least two seats to be reserved
      create a string of '0' and '1' where zero means empty seat and one - occupied.
      After such strings are created then look for "101" substring.
    */
    val stringRepresentations = groupedByRow
      .filter(_._2.length > 1) // if there is less than 2 seats occupied, then it is impossible to have such situation
      .map { case (_, columns) => 
        val indexes = (0 to room.columns).toList
        indexes.foldLeft("") { case (bits, index) =>
          if (columns.contains(index)) bits + "1"
          else bits + "0"
        }
      }

    stringRepresentations.find(_.contains("101")).isDefined
  }

  private def calculateAmountToPay(
    request: ReservationRequest,
    screening: Screening,
    voucherOpt: Option[Voucher]
  ): BigDecimal = {
    val basicPrice = request.seats.map(_.ticketType.price).sum
    val weekendBonus = 
      if (isWeekend(screening.time))
        BigDecimal(Config.AmountHigherDuringWeekend * request.seats.length)
      else BigDecimal(0)
    val price = basicPrice + weekendBonus

    voucherOpt match {
      case None         => price
      case Some(value)  => price * Config.FractionAfterVoucherApplication
    }
  }

  private def isWeekend(dateTime: LocalDateTime): Boolean = {
    import DayOfWeek._
    dateTime.getDayOfWeek match{
      case FRIDAY   => dateTime.getHour >= 17
      case SATURDAY => true
      case SUNDAY   => dateTime.getHour <= 23
      case _        => false
    }
  }

}

object MultiplexHandlerBasic {
  def layer: ZLayer[Any, Nothing, MultiplexHandlerBasic] =
    ZLayer.fromZIO(ZIO.succeed(new MultiplexHandlerBasic()))
}
