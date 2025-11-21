package kr.hhplus.be.server.concert.repository;

import kr.hhplus.be.server.concert.entity.ConcertDate;
import kr.hhplus.be.server.concert.entity.Seat;

import java.util.List;
import java.util.UUID;

public interface ConcertRepository {
    List<ConcertDate> findDatesByConcertId(UUID concertId);
    List<Seat> findSeatsByConcertDateId(UUID concertDateId);
}
