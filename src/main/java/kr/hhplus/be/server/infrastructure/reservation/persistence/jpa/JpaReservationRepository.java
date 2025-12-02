package kr.hhplus.be.server.infrastructure.reservation.persistence.jpa;

import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaReservationRepository extends JpaRepository<Reservation, UUID> {
    Optional<Reservation> findBySeatIdAndActive(UUID seatId, List<ReservationStatus> statuses);
}
