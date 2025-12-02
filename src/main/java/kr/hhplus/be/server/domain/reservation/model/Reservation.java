package kr.hhplus.be.server.domain.reservation.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
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

    public static Reservation create(User user, Seat seat, Clock clock, ReservationExpirationPolicy expirationPolicy) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expires = expirationPolicy.expiresAt(now);

        return Reservation.builder()
                .id(UUID.randomUUID())
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_HOLD)
                .tempHoldExpiresAt(expires)
                .confirmedAt(null)
                .deleted(false)
                .build();
    }
}
