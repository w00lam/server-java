package kr.hhplus.be.server.application.point.port.in;

import java.util.UUID;

public record ChargePointResult(UUID userId, int balance) {
}
