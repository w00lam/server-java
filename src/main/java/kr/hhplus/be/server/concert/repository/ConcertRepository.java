package kr.hhplus.be.server.concert.repository;

import kr.hhplus.be.server.concert.domain.ConcertDate;
import kr.hhplus.be.server.concert.domain.Seat;

import java.util.List;
import java.util.UUID;

public interface ConcertRepository {
    List<ConcertDate> findDatesByConcertId(UUID concertId);
    List<Seat> findSeatsByConcertDateId(UUID concertDateId);
}
