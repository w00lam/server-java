package kr.hhplus.be.server.infrastructure.concert.persistence.jpa;

import kr.hhplus.be.server.domain.concert.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaSeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findAllByConcertDate_Id(UUID concertDateId);
}
