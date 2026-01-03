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
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "QUEUE_TOKENS", uniqueConstraints = {@UniqueConstraint(columnNames = "token_value"), @UniqueConstraint(columnNames = "position")})
public class Token {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_value", nullable = false, unique = true)
    private String tokenValue;

    // 🔥 DB가 채우는 컬럼
    @Column(name = "position", nullable = false, insertable = false, updatable = false)
    private Integer position;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @PrePersist
    public void prePersist() {
        if (this.tokenValue == null && this.id != null) {
            this.tokenValue = String.valueOf(this.id);
        }
    }

    public static Token issue(User user) {
        return Token.builder()
                .user(user)
                .deleted(false)
                .build();
    }
}
