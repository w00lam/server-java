package kr.hhplus.be.server.queue.token.repository;

import kr.hhplus.be.server.queue.token.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaTokenRepository extends JpaRepository<Token, UUID> {
    Optional<Token> findTopByOrderByPositionDesc();
}
