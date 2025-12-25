package kr.hhplus.be.server.application.reservation.port.out;

import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ReservationRepositoryPort {
    Reservation findById(UUID reservationId);

    boolean confirmIfNotExpired(UUID reservationId);

    int expireAllExpired();

    Reservation save(Reservation reservation);

    boolean existsBySeatAndStatus(Seat seat, ReservationStatus status);

    long countBySeatAndStatus(Seat seat, ReservationStatus status);
}
