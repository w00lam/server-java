package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    // 특정 날짜의 활성 좌석 ID 조회 (TEMP_HOLD, CONFIRMED 상태)
    @Query("SELECT r.seat.id FROM Reservation r " +
            "WHERE r.seat.concertDate.id = :concertDateId " +
            "AND r.status IN ('TEMP_HOLD','CONFIRMED') " +
            "AND r.deleted = false")
    List<UUID> findActiveSeatIdsByConcertDate(@Param("concertDateId") UUID concertDateId);
}
