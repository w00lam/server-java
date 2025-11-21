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
@Table(name = "seats", uniqueConstraints = {@UniqueConstraint(columnNames = {"concertDateId", "section", "row", "number"})})
public class Seat {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID concertDateId;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private String row;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String grade;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    private boolean deleted = false;
}