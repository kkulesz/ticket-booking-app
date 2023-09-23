package domain

import java.util.UUID

final case class Reservation(
    screeningId: UUID,
    row: Int,
    column: Int,
    name: String,
    surname: String,
    ticketType: TicketType
    
)
