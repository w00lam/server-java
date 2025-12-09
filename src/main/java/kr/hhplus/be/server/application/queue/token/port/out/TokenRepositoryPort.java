package kr.hhplus.be.server.application.queue.token.port.out;

import kr.hhplus.be.server.domain.queue.token.model.Token;

public interface TokenRepositoryPort {
    Token save(Token token);
}
