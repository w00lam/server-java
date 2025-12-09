package kr.hhplus.be.server.domain.queue.token.service;

import kr.hhplus.be.server.domain.queue.token.model.Token;
import kr.hhplus.be.server.domain.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class TokenDomainService {
    public Token issueToken(User user) {
        return Token.issue(user);
    }
}
