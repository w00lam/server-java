package kr.hhplus.be.server.reservation.domain;

import java.time.LocalDateTime;

public interface ReservationExpirationPolicy {
    LocalDateTime expiresAt(LocalDateTime createdAt);
}
