package kr.hhplus.be.server.infrastructure.persistence.concert.adapter;

import kr.hhplus.be.server.application.concert.port.out.ConcertRepositoryPort;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.infrastructure.persistence.jpa.JpaConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepositoryPort {
    private final JpaConcertRepository jpa;

    @Override
    public Concert save(Concert concert) {
        return jpa.save(concert);
    }
}
