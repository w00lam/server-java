package kr.hhplus.be.server.queue.token.service;

import kr.hhplus.be.server.queue.token.domain.Token;
import kr.hhplus.be.server.queue.token.repository.TokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenService 단위 테스트")
public class TokenServiceTest {
    @Mock
    private TokenRepository repository;

    @InjectMocks
    private TokenService service;

    private static final UUID FIXED_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    @DisplayName("새 토큰 생성 시 position이 1 증가하고 토큰 생성")
    void createToken_newToken_success() {
        when(repository.findMaxPosition()).thenReturn(Optional.of(5));

        Token token = service.createToken(FIXED_USER_ID);

        assertThat(token.getUserId()).isEqualTo(FIXED_USER_ID);
        assertThat(token.getPosition()).isEqualTo(6); // 마지막 position + 1
        assertThat(token.getToken()).isNotNull();

        verify(repository, times(1)).save(any(Token.class));
    }
}
