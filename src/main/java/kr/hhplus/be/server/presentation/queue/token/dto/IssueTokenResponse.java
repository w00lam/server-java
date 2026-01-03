package kr.hhplus.be.server.presentation.queue.token.dto;

import kr.hhplus.be.server.application.queue.token.port.in.IssueTokenResult;

public record IssueTokenResponse(String token) {

    public static IssueTokenResponse from(IssueTokenResult result) {
        return new IssueTokenResponse(result.tokenValue());
    }
}