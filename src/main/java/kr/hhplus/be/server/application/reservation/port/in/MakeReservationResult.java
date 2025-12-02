package kr.hhplus.be.server.application.reservation.port.in;

import java.time.LocalDateTime;
import java.util.UUID;

public record MakeReservationResult(UUID reservationId, UUID userId, UUID seatId, String status,
                                    LocalDateTime tempHoldExpiresAt) {
}
