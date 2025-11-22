package kr.hhplus.be.server.reservation.domian;

import java.time.LocalDateTime;

public interface ReservationExpirationPolicy {
    LocalDateTime expiresAt(LocalDateTime createdAt);
}
