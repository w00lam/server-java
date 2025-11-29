package kr.hhplus.be.server.reservation.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.domain.Seat;
import kr.hhplus.be.server.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    private LocalDateTime tempHoldExpiresAt;

    private LocalDateTime confirmedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted;

    // 💡 팩토리 메서드
    public static Reservation create(User user, Seat seat, Clock clock, ReservationExpirationPolicy expirationPolicy) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expires = expirationPolicy.expiresAt(now);

        Reservation reservation = new Reservation();
        reservation.id = UUID.randomUUID();
        reservation.user = user;
        reservation.seat = seat;
        reservation.status = ReservationStatus.TEMP_HOLD;
        reservation.tempHoldExpiresAt = expires;
        reservation.confirmedAt = null;
        reservation.createdAt = now;
        reservation.updatedAt = now;
        reservation.deleted = false;

        return reservation;
    }
}
