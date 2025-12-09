package kr.hhplus.be.server.application.point.port.in;

import java.util.UUID;

public record ChargePointCommand(UUID userId, int amount) {
}
