package domain

import zio.json._
import doobie.util.{Read, Write}

sealed trait TicketType extends Product with Serializable {
  def asString: String
  def price: BigDecimal
}

object TicketType {
  final case object Adult extends TicketType {
    val asString = "Adult"; val price = 25.00
  }
  final case object Student extends TicketType {
    val asString = "Student"; val price = 18.00
  }
  final case object Child extends TicketType {
    val asString = "Child"; val price = 12.50
  }

  implicit val jsonEncoder: JsonEncoder[TicketType] =
    JsonEncoder.string.contramap(_.asString)

  implicit val jsonDecoder: JsonDecoder[TicketType] =
    JsonDecoder[String].map(s => TicketType.fromString(s))

  def fromString(str: String): TicketType = str match {
    case Adult.asString   => Adult
    case Student.asString => Student
    case Child.asString   => Child
    case _: String        => Adult // default is adult
  }

  implicit val ticketTypeRead: Read[TicketType] =
    Read[String].map(s => TicketType.fromString(s))

  implicit val ticketTypeWrite: Write[TicketType] = 
    Write[String].contramap(_.asString)
}
