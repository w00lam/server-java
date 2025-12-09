package kr.hhplus.be.server.presentation.point.dto;

import kr.hhplus.be.server.application.point.port.in.GetPointResult;

import java.util.UUID;

public record GetPointResponse(UUID userId, int balance) {
    public static GetPointResponse from(GetPointResult result) {
        return new GetPointResponse(result.userId(), result.balance());
    }
}
