package kr.hhplus.be.server.infrastructure.queue.token.persistence.jpa;

import kr.hhplus.be.server.domain.queue.token.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaTokenRepository extends JpaRepository<Token, UUID> {
    Integer findTopByOrderByPositionDesc();
}
