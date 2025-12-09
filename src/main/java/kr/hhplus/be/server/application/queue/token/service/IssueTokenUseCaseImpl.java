package kr.hhplus.be.server.application.queue.token.service;

import kr.hhplus.be.server.application.queue.token.port.in.IssueTokenCommand;
import kr.hhplus.be.server.application.queue.token.port.in.IssueTokenResult;
import kr.hhplus.be.server.application.queue.token.port.in.IssueTokenUseCase;
import kr.hhplus.be.server.application.queue.token.port.out.TokenRepositoryPort;
import kr.hhplus.be.server.application.user.port.out.UserRepositoryPort;
import kr.hhplus.be.server.domain.queue.token.model.Token;
import kr.hhplus.be.server.domain.queue.token.service.TokenDomainService;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueTokenUseCaseImpl implements IssueTokenUseCase {
    private final TokenRepositoryPort tokenRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final TokenDomainService tokenDomainService;

    @Override
    public IssueTokenResult execute(IssueTokenCommand command) {
        User user = userRepositoryPort.findById(command.userID());
        Integer lastPosition = tokenRepositoryPort.findLastPosition();
        Token issued = tokenDomainService.issueToken(user, lastPosition);
        tokenRepositoryPort.save(issued);

        return new IssueTokenResult(issued.getTokenValue(), issued.getPosition());
    }
}
