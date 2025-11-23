package kr.hhplus.be.server.queue.token.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "QUEUE_TOKENS",
        uniqueConstraints = @UniqueConstraint(columnNames = "token"))
public class Token {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "position", nullable = false)
    private int position;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted")
    private Boolean deleted;

    public static Token create(UUID userId, String token, int position) {
        return Token.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .token(token)
                .position(position)
                .deleted(false)
                .build();
    }
}
