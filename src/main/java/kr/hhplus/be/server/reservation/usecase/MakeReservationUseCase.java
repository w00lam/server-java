package kr.hhplus.be.server.reservation.usecase;

import kr.hhplus.be.server.reservation.domain.Reservation;

import kr.hhplus.be.server.reservation.service.ReservationServiceImpl;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class MakeReservationUseCase {
    private final ReservationServiceImpl service;

    public Reservation makeReservation(UUID userId, UUID seatId) {
        return service.reserveSeat(userId, seatId);
    }
}
