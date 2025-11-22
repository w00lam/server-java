package kr.hhplus.be.server.reservation.repository;

import kr.hhplus.be.server.reservation.domian.Reservation;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {
    // 좌석 ID로 활성 예약 조회
    Optional<Reservation> findBySeatIdAndActive(UUID setId);
    // 예약 저장
    void save(Reservation reservation);
}
