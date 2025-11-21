package kr.hhplus.be.server.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "concert_dates")
public class ConcertDate {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID concertId;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate eventDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    private boolean deleted = false;
}
