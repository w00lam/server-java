package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.domain.ConcertDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConcertDateRepository extends JpaRepository<ConcertDate, UUID> {
    // 활성화된 날짜를 오름차순 정렬
    List<ConcertDate> findByConcertIdAndDeletedFalseOrderByEventDateAsc(UUID concertId);
}
