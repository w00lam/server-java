package kr.hhplus.be.server.application.reservation.port.in;

import java.util.UUID;

public record MakeReservationCommand(UUID userId, UUID concertId, UUID seatId) {
}
