package kr.hhplus.be.server.presentation.reservation.dto;

import java.util.UUID;

public record MakeReservationRequest(UUID userId, UUID seatId) {
}