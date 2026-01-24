package kr.hhplus.be.server.application.concert.service;

import kr.hhplus.be.server.application.concert.port.in.concertdate.GetConcertDatesQuery;
import kr.hhplus.be.server.application.concert.port.in.concertdate.GetConcertDatesResult;
import kr.hhplus.be.server.application.concert.port.in.concertdate.GetConcertDatesUseCase;
import kr.hhplus.be.server.application.concert.port.out.ConcertDateRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetConcertDatesUseCaseImpl implements GetConcertDatesUseCase {
    private final ConcertDateRepositoryPort concertDateRepositoryPort;

    @Override
    @Cacheable(value = "concertDates", key = "#query.concertId")
    public List<GetConcertDatesResult> execute(GetConcertDatesQuery query) {
        return concertDateRepositoryPort.findDatesByConcertId(query.concertId())
                .stream()
                .map(date -> new GetConcertDatesResult(date.getId(), date.getEventDate()))
                .toList();
    }
}
