package kr.hhplus.be.server.application.concert.port.out;

import kr.hhplus.be.server.application.concert.service.ConcertRankingItem;

import java.util.List;
import java.util.UUID;

public interface ConcertRankingRepositoryPort {
    void increase(UUID concertId, long delta);

    void decrease(UUID concertId, long delta);

    List<ConcertRankingItem> findTopRanked(int limit);
}
