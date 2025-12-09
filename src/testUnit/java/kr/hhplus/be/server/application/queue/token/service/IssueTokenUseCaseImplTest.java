package kr.hhplus.be.server.application.queue.token.service;

import kr.hhplus.be.server.application.queue.token.port.in.IssueTokenCommand;
import kr.hhplus.be.server.application.queue.token.port.in.IssueTokenResult;
import kr.hhplus.be.server.application.queue.token.port.out.TokenRepositoryPort;
import kr.hhplus.be.server.application.user.port.out.UserRepositoryPort;
import kr.hhplus.be.server.domain.queue.token.model.Token;
import kr.hhplus.be.server.domain.queue.token.service.TokenDomainService;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.common.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IssueTokenUseCaseImplTest extends BaseUnitTest {
    @Mock
    TokenRepositoryPort tokenRepositoryPort;

    @Mock
    UserRepositoryPort userRepositoryPort;

    @Mock
    TokenDomainService tokenDomainService;

    @InjectMocks
    IssueTokenUseCaseImpl useCase;

    @Test
    @DisplayName("토큰 발급 시 User와 마지막 Position 기반으로 새로운 토큰이 생성되고 저장된다")
    void execute_success() {
        // given
        User user = User.builder().id(fixedUUID()).email("test@test.com").name("Test User").build();
        IssueTokenCommand command = new IssueTokenCommand(user.getId());

        when(userRepositoryPort.findById(user.getId())).thenReturn(user);

        Token issuedToken = Token.issue(user);

        // when
        IssueTokenResult result = useCase.execute(command);

        // then
        assertEquals(issuedToken.getTokenValue(), result.tokenValue());
        assertEquals(issuedToken.getPosition(), result.position());

        verify(userRepositoryPort).findById(user.getId());
        verify(tokenDomainService).issueToken(user);
        verify(tokenRepositoryPort).save(issuedToken);
    }
}
