package kr.hhplus.be.server.reservation.entity;

import java.time.LocalDateTime;

public interface ReservationExpirationPolicy {
    LocalDateTime expiresAt(LocalDateTime createdAt);
}
