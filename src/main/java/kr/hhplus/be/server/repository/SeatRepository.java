package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID> {
    // 삭제되지 않은 좌석 조회
    List<Seat> findByConcertDateIdAndDeletedFalse(UUID concertDateId);
}
