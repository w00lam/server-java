package kr.hhplus.be.server.infrastructure.queue.token.persistence;

import kr.hhplus.be.server.application.queue.token.port.out.TokenRepositoryPort;
import kr.hhplus.be.server.domain.queue.token.model.Token;
import kr.hhplus.be.server.infrastructure.queue.token.persistence.jpa.JpaTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepositoryPort {
    private final JpaTokenRepository jpa;

    @Override
    public Token save(Token token) {
        return jpa.save(token);
    }

    @Override
    public Integer findLastPosition() {
        return jpa.findTopByOrderByPositionDesc();
    }
}
