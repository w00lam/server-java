package kr.hhplus.be.server.adapter.out.persistence;

import kr.hhplus.be.server.adapter.out.persistence.jpa.JpaReservationRepository;
import kr.hhplus.be.server.reservation.domian.Reservation;
import kr.hhplus.be.server.reservation.domian.ReservationStatus;
import kr.hhplus.be.server.reservation.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    private final JpaReservationRepository jpaRepository;

    @Override
    public Optional<Reservation> findBySeatIdAndActive(UUID seatId) {
        return jpaRepository.findBySeatIdAndActive(seatId, List.of(ReservationStatus.TEMP_HOLD, ReservationStatus.CONFIRMED));
    }

    @Override
    public void save(Reservation reservation) {
        jpaRepository.save(reservation);
    }
}
