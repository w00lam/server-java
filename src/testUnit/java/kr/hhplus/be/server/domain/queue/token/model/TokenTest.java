package kr.hhplus.be.server.domain.queue.token.model;

import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.common.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TokenTest extends BaseUnitTest {
    @Test
    @DisplayName("issue()는 새로운 토큰을 발급해야 하며 id·tokenValue·position·deleted가 올바르게 설정된다")
    void issue_shouldCreateTokenCorrectly() {
        // given
        User user = User.builder()
                .id(fixedUUID())
                .build();

        int position = 12;

        // when
        Token token = Token.issue(user, position);

        // then
        assertNotNull(token.getId(), "issue는 UUID를 생성해야 한다");
        assertEquals(user, token.getUser());
        assertEquals(position, token.getPosition());
        assertEquals(token.getId().toString(), token.getTokenValue());
        assertFalse(token.getDeleted());

        // Auditing → null
        assertNull(token.getCreatedAt());
        assertNull(token.getUpdatedAt());
    }

    @Test
    @DisplayName("Builder로 Token을 생성하면 모든 필드가 지정된 값으로 설정되어야 한다")
    void builder_shouldSetAllFieldsCorrectly() {
        // given
        UUID id = fixedUUID();
        User user = User.builder().id(fixedUUID()).build();

        // when
        Token token = Token.builder()
                .id(id)
                .user(user)
                .tokenValue("abc-123-token")
                .position(5)
                .deleted(true)
                .build();

        // then
        assertEquals(id, token.getId());
        assertEquals(user, token.getUser());
        assertEquals("abc-123-token", token.getTokenValue());
        assertEquals(5, token.getPosition());
        assertTrue(token.getDeleted());

        // Auditing → null
        assertNull(token.getCreatedAt());
        assertNull(token.getUpdatedAt());
    }
}
