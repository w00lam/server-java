package kr.hhplus.be.server.domain.queue.token.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
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

    @Column(name = "token_value", nullable = false, unique = true)
    private String tokenValue;

    @Column(name = "position", nullable = false)
    private int position;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    public static Token issue(User user, int position) {
        UUID id = UUID.randomUUID();

        return Token.builder()
                .id(id)
                .user(user)
                .tokenValue(id.toString())
                .position(position)
                .deleted(false)
                .build();
    }
}
