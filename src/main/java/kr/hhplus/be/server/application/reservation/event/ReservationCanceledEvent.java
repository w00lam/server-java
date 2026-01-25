package kr.hhplus.be.server.application.reservation.event;

import java.util.UUID;

public record ReservationCanceledEvent(UUID reservationId, UUID concertId) {
}
