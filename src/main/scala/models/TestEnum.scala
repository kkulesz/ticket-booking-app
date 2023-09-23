package models

import zio.json._

sealed trait MyTestEnum extends Product with Serializable {
  def asString: String
}

object MyTestEnum {
  final case object Enum1 extends MyTestEnum { val asString = "Enum 1" }
  final case object Enum2 extends MyTestEnum { val asString = "Enum 2" }
  final case object Enum3 extends MyTestEnum { val asString = "Enum 3" }

  implicit val jsonEncoder: JsonEncoder[MyTestEnum] =
    JsonEncoder.string.contramap(_.asString)

  implicit val jsonDecoder: JsonDecoder[MyTestEnum] = JsonDecoder[String].map {
    case Enum1.asString => Enum1
    case Enum2.asString => Enum2
    case Enum3.asString => Enum3
    case other =>
      throw new IllegalArgumentException(s"Invalid MyTestEnum value: $other")
  }
}
