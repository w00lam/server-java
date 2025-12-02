package kr.hhplus.be.server.presentation.point.dto;

import kr.hhplus.be.server.application.point.port.in.ChargePointResult;

import java.util.UUID;

public record ChargePointResponse(UUID userId, int balance) {
    public static ChargePointResponse from(ChargePointResult result) {
        return new ChargePointResponse(result.userId(), result.balance());
    }
}
