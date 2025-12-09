package kr.hhplus.be.server.application.concert.port.out;

import kr.hhplus.be.server.domain.concert.model.Concert;

public interface ConcertRepositoryPort {
    Concert save(Concert concert);
}
