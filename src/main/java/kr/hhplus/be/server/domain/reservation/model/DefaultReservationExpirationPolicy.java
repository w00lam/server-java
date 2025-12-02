package kr.hhplus.be.server.domain.reservation.model;

import java.time.LocalDateTime;

public class DefaultReservationExpirationPolicy implements ReservationExpirationPolicy {
    @Override
    public LocalDateTime expiresAt(LocalDateTime createdAt) {
        return createdAt.plusMinutes(5); // 기본 5분
    }
}
