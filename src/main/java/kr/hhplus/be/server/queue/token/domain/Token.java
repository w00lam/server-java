package kr.hhplus.be.server.queue.token.domain;

import jakarta.persistence.*;
import kr.hhplus.be.server.user.domain.User;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public static Token create(User user, String token, int position) {
        return Token.builder()
                .id(UUID.randomUUID())
                .user(user)
                .token(token)
                .position(position)
                .deleted(false)
                .build();
    }
}
