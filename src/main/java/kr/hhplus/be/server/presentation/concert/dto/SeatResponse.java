package kr.hhplus.be.server.presentation.concert.dto;

import kr.hhplus.be.server.application.concert.port.in.GetSeatsResult;

import java.util.UUID;

public record SeatResponse(
        UUID seatId,
        String section,
        String row,
        String number,
        String grade
) {
    public static SeatResponse from(GetSeatsResult result) {
        return new SeatResponse(
                result.seatId(),
                result.section(),
                result.row(),
                result.number(),
                result.grade()
        );
    }
}