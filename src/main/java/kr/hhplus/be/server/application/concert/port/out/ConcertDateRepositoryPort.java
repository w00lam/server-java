package kr.hhplus.be.server.application.concert.port.out;

import kr.hhplus.be.server.domain.concert.model.ConcertDate;

import java.util.List;
import java.util.UUID;

public interface ConcertDateRepositoryPort {
    ConcertDate save(ConcertDate concertDate);
    List<ConcertDate> findDatesByConcertId(UUID concertId);
}
