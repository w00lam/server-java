package kr.hhplus.be.server.application.point.port.in;

import java.util.UUID;

public record DeductPointResult(UUID userId, int deductedAmount, int remainingPoints) {
}
