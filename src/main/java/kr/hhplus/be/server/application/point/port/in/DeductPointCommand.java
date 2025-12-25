package kr.hhplus.be.server.application.point.port.in;

import java.util.UUID;

public record DeductPointCommand(UUID userId, int amount) {
}
