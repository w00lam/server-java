package kr.hhplus.be.server.application.reservation.port.out;

import kr.hhplus.be.server.domain.reservation.model.Reservation;

import java.util.UUID;

public interface ReservationRepositoryPort {
    Reservation findById(UUID reservationId);

    Reservation save(Reservation reservation);
}
