package kr.hhplus.be.server.queue.token.repository;

import kr.hhplus.be.server.queue.token.domain.Token;

import java.util.Optional;

public interface TokenRepository {
    Token save(Token token);
    Optional<Integer> findMaxPosition();
}
