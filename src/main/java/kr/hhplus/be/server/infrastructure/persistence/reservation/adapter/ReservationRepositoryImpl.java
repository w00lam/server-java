package kr.hhplus.be.server.infrastructure.persistence.reservation.adapter;

import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.infrastructure.persistence.jpa.JpaReservationRepository;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryPort {
    private final JpaReservationRepository jpa;

    @Override
    public Reservation findById(UUID reservationId) {
        return jpa.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
    }

    @Override
    public boolean confirmIfNotExpired(UUID reservationId) {
        return jpa.confirmIfNotExpired(reservationId) == 1;
    }

    @Override
    public int expireAllExpired() {
        return jpa.expireAllExpired();
    }

    @Override
    public Reservation save(Reservation reservation) {
        return jpa.save(reservation);
    }

    @Override
    public boolean existsBySeatAndStatus(Seat seat, ReservationStatus status) {
        return jpa.existsBySeatAndStatus(seat, status);
    }

    @Override
    public long countBySeatAndStatus(Seat seat, ReservationStatus status) {
        return jpa.countBySeatAndStatus(seat, status);
    }
}
