package kr.hhplus.be.server.reservation.repository;

import kr.hhplus.be.server.reservation.domain.Reservation;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository {
    // 좌석 ID로 활성 예약 조회
    Optional<Reservation> findBySeatIdAndActive(UUID setId);
    // 예약 저장
    void save(Reservation reservation);
}
