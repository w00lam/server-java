package kr.hhplus.be.server.concert.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "concerts")
public class Concert {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ConcertDate와 양방향 연관관계
    @OneToMany(
            mappedBy = "concert",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ConcertDate> dates = new ArrayList<>();

    // 연관 엔티티 추가 헬퍼 메서드
    public void addDate(ConcertDate date) {
        dates.add(date);
        date.setConcert(this);
    }

    public void removeDate(ConcertDate date) {
        dates.remove(date);
        date.setConcert(null);
    }
}
