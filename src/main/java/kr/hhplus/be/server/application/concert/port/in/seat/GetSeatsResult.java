package kr.hhplus.be.server.application.concert.port.in.seat;

import java.util.UUID;

public record GetSeatsResult(UUID seatId, String section, String row, String number, String grade) {
}
