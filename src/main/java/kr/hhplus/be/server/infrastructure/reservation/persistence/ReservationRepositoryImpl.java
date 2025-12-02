package kr.hhplus.be.server.infrastructure.reservation.persistence;

import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.infrastructure.reservation.persistence.jpa.JpaReservationRepository;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryPort {
    private final JpaReservationRepository jpa;

    @Override
    public Reservation findById(UUID reservationId) {
        return jpa.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
    }

    @Override
    public Reservation save(Reservation reservation) {
        return jpa.save(reservation);
    }
}
