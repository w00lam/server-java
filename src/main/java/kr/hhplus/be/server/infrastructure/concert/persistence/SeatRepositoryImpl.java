package kr.hhplus.be.server.infrastructure.concert.persistence;

import kr.hhplus.be.server.application.concert.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.infrastructure.concert.persistence.jpa.JpaSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepositoryPort {
    private final JpaSeatRepository jpa;

    @Override
    public Seat findById(UUID seatId) {
        return jpa.findById(seatId).orElseThrow(()->new IllegalArgumentException("Seat not found: " + seatId));
    }

    @Override
    public List<Seat> findSeatsByConcertDateId(UUID concertDateId) {
        return jpa.findAllByConcertDate_Id(concertDateId);
    }
}
