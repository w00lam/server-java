package kr.hhplus.be.server.queue.token.service;

import kr.hhplus.be.server.queue.token.domain.Token;
import kr.hhplus.be.server.queue.token.repository.TokenRepositoryImpl;
import kr.hhplus.be.server.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepositoryImpl repository;

    public Token createToken(User user) {
        int nextPosition = repository.findMaxPosition().orElse(0) + 1;
        String tokenValue = UUID.randomUUID().toString();

        Token token = Token.create(user, tokenValue, nextPosition);
        return repository.save(token);
    }
}
