package kr.hhplus.be.server.application.concert.port.in.seat;

import java.util.UUID;

public record GetSeatsQuery(UUID concertDateId) {
}
