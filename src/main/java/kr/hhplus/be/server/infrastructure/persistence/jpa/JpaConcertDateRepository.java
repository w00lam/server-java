package kr.hhplus.be.server.infrastructure.persistence.jpa;

import kr.hhplus.be.server.domain.concert.model.ConcertDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaConcertDateRepository extends JpaRepository<ConcertDate, UUID> {
    List<ConcertDate> findAllByConcert_Id(UUID concertId);
}
