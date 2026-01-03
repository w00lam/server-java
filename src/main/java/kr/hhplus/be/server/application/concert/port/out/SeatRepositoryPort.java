package kr.hhplus.be.server.application.concert.port.out;

import kr.hhplus.be.server.domain.concert.model.Seat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SeatRepositoryPort {
    Seat save(Seat seat);
    Seat findById(UUID seatId);

    Seat findByIdForUpdate(UUID seatId);

    List<Seat> findSeatsByConcertDateId(UUID concertDateId);

    List<Seat> findSeatsByConcertDateIdForHoldRelease(LocalDateTime now);
}
