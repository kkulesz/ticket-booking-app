package models.domainModel

import java.util.UUID

final case class ScreeningReservations(screeningId: UUID, seat: (Int, Int))
