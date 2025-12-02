package kr.hhplus.be.server.domain.queue.token.service;

import kr.hhplus.be.server.domain.queue.token.model.Token;
import kr.hhplus.be.server.domain.user.model.User;

public class TokenDomainService {
    public Token issueToken(User user, Integer lastPosition) {
        int nextPosition = (lastPosition == null) ? 0 : lastPosition + 1;
        return Token.issue(user, nextPosition);
    }
}
