package domain

import zio.json._

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

  implicit val jsonDecoder: JsonDecoder[TicketType] = JsonDecoder[String].map {
    case Adult.asString   => Adult
    case Student.asString => Student
    case Child.asString   => Child
    case other =>
      throw new IllegalArgumentException(s"Invalid TicketType value: $other")
  }
}
