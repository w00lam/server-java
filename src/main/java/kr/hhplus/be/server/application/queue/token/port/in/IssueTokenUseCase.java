package kr.hhplus.be.server.application.queue.token.port.in;

public interface IssueTokenUseCase {
    IssueTokenResult execute(IssueTokenCommand command);
}
