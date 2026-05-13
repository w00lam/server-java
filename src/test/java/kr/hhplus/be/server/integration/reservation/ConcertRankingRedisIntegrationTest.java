package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.concert.application.port.out.ConcertRankingRepositoryPort;
import kr.hhplus.be.server.concert.application.service.ConcertRankingItem;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcertRankingRedisIntegrationTest extends ReservationIntegrationTestBase {
    private static final String RANKING_CONCERT_TITLE = "Redis Ranking Concert";

    @Autowired
    private ConcertRankingRepositoryPort concertRankingRepository;

    @Test
    @DisplayName("결제가 완료되면 콘서트 예약 랭킹에 반영된다")
    void paidReservationIncreasesConcertRanking() {
        User user = createUserWithPoints(10_000);
        ReservedSeat reservedSeat = createReservedSeat(user, RANKING_CONCERT_TITLE);

        payReservation(reservedSeat.reservationId(), 5_000, PaymentMethod.CARD);

        List<ConcertRankingItem> rankings = concertRankingRepository.findTopRanked(10);

        assertThat(rankings)
                .anySatisfy(ranking -> {
                    assertThat(ranking.concertId()).isEqualTo(reservedSeat.concertId());
                    assertThat(ranking.reservationCount()).isEqualTo(1L);
                });
    }
}
