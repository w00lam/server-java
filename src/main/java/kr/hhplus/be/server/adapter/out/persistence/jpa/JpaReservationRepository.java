package kr.hhplus.be.server.adapter.out.persistence.jpa;

import kr.hhplus.be.server.reservation.entity.Reservation;
import kr.hhplus.be.server.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaReservationRepository extends JpaRepository<Reservation, UUID> {
    Optional<Reservation> findBySeatIdAndActive(UUID seatId, List<ReservationStatus> statuses);
}
