package kr.hhplus.be.server.domain.queue.token.service;

import kr.hhplus.be.server.domain.queue.token.model.Token;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.test.unit.BaseUnitTest;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TokenDomainServiceTest extends BaseUnitTest {
    private final TokenDomainService service = new TokenDomainService();

    @Test
    @DisplayName("issueToken: lastPosition이 null이면 position 0으로 발급")
    void issueToken_nullLastPosition() {
        User user = User.builder().id(fixedUUID()).build();
        Token token = service.issueToken(user, null);

        assertNotNull(token);
        assertEquals(user, token.getUser());
        assertEquals(0, token.getPosition());
        assertEquals(token.getId().toString(), token.getTokenValue());
        assertFalse(token.getDeleted());
    }

    @Test
    @DisplayName("issueToken: lastPosition이 존재하면 nextPosition 발급")
    void issueToken_withLastPosition() {
        User user = User.builder().id(fixedUUID()).build();
        Token token = service.issueToken(user, 5);

        assertNotNull(token);
        assertEquals(user, token.getUser());
        assertEquals(6, token.getPosition());
        assertEquals(token.getId().toString(), token.getTokenValue());
        assertFalse(token.getDeleted());
    }
}
