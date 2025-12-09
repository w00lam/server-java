package kr.hhplus.be.server.application.reservation.port.in;

import java.util.UUID;

public record ConfirmReservationCommand(UUID reservationId) {
}
