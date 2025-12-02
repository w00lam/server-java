package kr.hhplus.be.server.presentation.queue.token.dto;

import kr.hhplus.be.server.domain.user.model.User;

public record IssueTokenRequest(User user) {
}

