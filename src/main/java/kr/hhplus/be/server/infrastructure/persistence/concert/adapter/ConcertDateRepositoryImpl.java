package kr.hhplus.be.server.infrastructure.persistence.concert.adapter;

import kr.hhplus.be.server.application.concert.port.out.ConcertDateRepositoryPort;
import kr.hhplus.be.server.domain.concert.model.ConcertDate;
import kr.hhplus.be.server.infrastructure.persistence.jpa.JpaConcertDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConcertDateRepositoryImpl implements ConcertDateRepositoryPort {
    private final JpaConcertDateRepository jpa;


    @Override
    public ConcertDate save(ConcertDate concertDate) {
        return jpa.save(concertDate);
    }

    @Override
    public List<ConcertDate> findDatesByConcertId(UUID concertId) {
        return jpa.findAllByConcert_Id(concertId);
    }
}
