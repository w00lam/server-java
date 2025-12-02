package kr.hhplus.be.server.application.queue.token.port.in;

import java.util.UUID;

public record IssueTokenCommand(UUID userID) {
}
