package domain

import zio.json._
import java.util.UUID

final case class Voucher(id: UUID, isUsed: Boolean)

object Voucher {
  implicit val decoder: JsonDecoder[Voucher] =
    JsonDecoder[String].map(s => Voucher(UUID.fromString(s), false))
  implicit val encoder: JsonEncoder[Voucher] =
    JsonEncoder.string.contramap(_.id.toString)

}
