package kr.hhplus.be.server.queue.token.controller;

import kr.hhplus.be.server.queue.token.domain.Token;
import kr.hhplus.be.server.queue.token.dto.TokenRequest;
import kr.hhplus.be.server.queue.token.service.TokenService;
import kr.hhplus.be.server.user.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenControllerTest 단위 테스트")
public class TokenControllerTest {
    @Mock
    private TokenService service;

    @InjectMocks
    private TokenController controller;

    private static final UUID FIXED_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private User createUserFixture() {
        User user = new User();
        user.setId(FIXED_USER_ID);
        return user;
    }

    @Test
    @DisplayName("토큰 발급 성공 시 201 Created 반환")
    void issueToken_success() {
        User user = createUserFixture();
        TokenRequest request = new TokenRequest(user);

        Token token = Token.create(user, "token-abc", 5);

        when(service.createToken(user)).thenReturn(token);

        var result = controller.issueToken(request);

        assertEquals(201, result.getStatusCode().value());
        Assertions.assertNotNull(result.getBody());
        assertEquals("token-abc", result.getBody().getToken());
        assertEquals(5, result.getBody().getPosition());
    }
}
