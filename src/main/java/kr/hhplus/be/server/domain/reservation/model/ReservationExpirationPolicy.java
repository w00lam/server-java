package kr.hhplus.be.server.domain.reservation.model;

import java.time.LocalDateTime;

public interface ReservationExpirationPolicy {
    LocalDateTime expiresAt(LocalDateTime createdAt);
}
