package kr.hhplus.be.server.queue.token.repository;

import kr.hhplus.be.server.queue.token.domain.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {
    private final JpaTokenRepository repository;

    @Override
    public Token save(Token token) {
        return repository.save(token);
    }

    @Override
    public Optional<Integer> findMaxPosition() {
        return repository.findTopByOrderByPositionDesc().map(Token::getPosition);
    }
}
