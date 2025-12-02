package kr.hhplus.be.server.infrastructure.concert.persistence.jpa;

import kr.hhplus.be.server.domain.concert.model.ConcertDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaConcertDateRepository extends JpaRepository<ConcertDate, UUID> {
    List<ConcertDate> findAllByConcert_Id(UUID concertId);
}
