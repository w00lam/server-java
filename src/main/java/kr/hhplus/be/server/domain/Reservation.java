package kr.hhplus.be.server.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations", uniqueConstraints = {@UniqueConstraint(columnNames = {"seatId", "status"})
})
public class Reservation {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID seatId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime tempHoldExpiresAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime confirmedAt = LocalDateTime.now();

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private boolean deleted = false;

    public enum Status {TEMP_HOLD, CONFIRMED, CANCELED, EXPIRED}
}