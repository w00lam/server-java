package kr.hhplus.be.server.concert.infrastructure.persistence;

import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
/**
 * Spring Data JPA repository for concert entities.
 */

@Repository
public interface JpaSeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findAllByConcertDate_Id(UUID concertDateId);
}
