package kr.hhplus.be.server.concert.domain;

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
@Table(
        name = "seats",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_concert_date_section_row_number",
                columnNames = {"concert_date_id", "section", "row", "number"}
        ),
        indexes = {@Index(name = "idx_concert_date_section_row",
                columnList = "concert_date_id, section, row")}
)
public class Seat {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "concert_date_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID concertDateId;

    @Column(name = "section", length = 20, nullable = false)
    private String section;

    @Column(name = "row", length = 5, nullable = false)
    private String row;

    @Column(name = "number", length = 5, nullable = false)
    private String number;

    @Column(name = "grade", length = 20, nullable = false)
    private String grade;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
