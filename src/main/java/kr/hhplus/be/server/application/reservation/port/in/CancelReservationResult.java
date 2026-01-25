package kr.hhplus.be.server.application.reservation.port.in;

import java.time.LocalDateTime;
import java.util.UUID;

public record CancelReservationResult(UUID reservationId, String status, LocalDateTime canceledAt) {
}
