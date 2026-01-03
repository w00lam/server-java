package kr.hhplus.be.server.infrastructure.persistence.queue.token.adapter;

import kr.hhplus.be.server.application.queue.token.port.out.TokenRepositoryPort;
import kr.hhplus.be.server.domain.queue.token.model.Token;
import kr.hhplus.be.server.infrastructure.persistence.jpa.JpaTokenRepository;
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
}
