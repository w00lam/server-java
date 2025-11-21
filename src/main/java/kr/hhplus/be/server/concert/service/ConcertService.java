package kr.hhplus.be.server.concert.service;

import kr.hhplus.be.server.concert.entity.ConcertDate;
import kr.hhplus.be.server.concert.entity.Seat;
import kr.hhplus.be.server.concert.repository.ConcertRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ConcertService {
    private final ConcertRepository repository;

    public List<ConcertDate> getConcertDates(UUID concertId) {
        return repository.findDatesByConcertId(concertId);
    }

    public List<Seat> getSeatsByConcertDate(UUID concertDateId) {
        return repository.findSeatsByConcertDateId(concertDateId);
    }
}
