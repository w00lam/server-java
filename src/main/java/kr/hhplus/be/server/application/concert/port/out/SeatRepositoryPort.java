package kr.hhplus.be.server.application.concert.port.out;

import kr.hhplus.be.server.domain.concert.model.Seat;

import java.util.List;
import java.util.UUID;

public interface SeatRepositoryPort {
    Seat findById(UUID seatId);

    List<Seat> findSeatsByConcertDateId(UUID concertDateId);
}
