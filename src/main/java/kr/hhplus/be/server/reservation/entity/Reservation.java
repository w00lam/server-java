package kr.hhplus.be.server.reservation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RESERVATIONS",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_seat_status",
                columnNames = {"seatId", "status"} // status가 TEMP_HOLD, CONFIRMED일 때만 체크는 DB 스키마에서 partial index 필요
        ),
        indexes = {
                @Index(name = "idx_status_tempHoldExpiresAt", columnList = "status,tempHoldExpiresAt")
        }
)
public class Reservation {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID seatId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    private LocalDateTime tempHoldExpiresAt;

    private LocalDateTime confirmedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted;

    // 💡 팩토리 메서드
    public static Reservation create(UUID userId, UUID seatId, Clock clock, ReservationExpirationPolicy expirationPolicy) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expires = expirationPolicy.expiresAt(now);

        return new Reservation(UUID.randomUUID(), userId, seatId, ReservationStatus.TEMP_HOLD, expires, null, now, now, false);
    }
}
