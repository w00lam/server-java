package kr.hhplus.be.server.application.reservation.event;

import java.util.UUID;

public record ReservationConfirmedEvent(UUID reservationId, UUID concertId) {
}
