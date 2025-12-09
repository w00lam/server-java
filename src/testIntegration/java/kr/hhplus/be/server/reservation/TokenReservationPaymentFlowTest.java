package kr.hhplus.be.server.reservation;

import kr.hhplus.be.server.application.payment.port.in.MakePaymentResult;
import kr.hhplus.be.server.application.queue.token.port.in.IssueTokenResult;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationResult;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertDate;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.payment.model.PaymentMethod;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.reservation.common.ReservationIntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenReservationPaymentFlowTest extends ReservationIntegrationTestBase {
    @Test
    @DisplayName("Token → Seat Reservation → Payment 정상 흐름 테스트")
    void testTokenReservationPaymentFlow() {
        // given: 좌석 생성
        User user = createUser();
        Concert concert = Concert.builder()
                .title("test_title")
                .build();
        concert = concertRepository.save(concert);
        ConcertDate concertDate = concertDateRepository.save(ConcertDate.create(concert, LocalDate.of(2035, 12, 12)));
        Seat seat = createSeatWithConcert(concertDate, "A", "1", "01", "VIP");

        // when: 토큰 발급
        IssueTokenResult tokenResult = issueToken(user);

        // then: 토큰 발급 확인
        assertThat(tokenResult).isNotNull();
        assertThat(tokenResult.tokenValue()).isNotEmpty();

        // when: 좌석 예약
        MakeReservationResult reservationResult = reserveSeat(user.getId(), seat.getId());

        // then: 예약 확인
        assertThat(reservationResult).isNotNull();
        assertThat(reservationResult.status()).isEqualTo("TEMP_HOLD");

        // when: 결제 진행
        MakePaymentResult paymentResult = payReservation(reservationResult.reservationId(), 100, PaymentMethod.CARD);

        // then: 결제 확인
        assertThat(paymentResult).isNotNull();
        assertThat(paymentResult.status()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Expired Seat → 다른 유저가 예약 가능")
    void testSeatReservationAfterExpiration() throws InterruptedException {
        // given
        User firstUser = createUser();
        Concert concert = Concert.builder()
                .title("test_title")
                .build();
        concert = concertRepository.save(concert);
        ConcertDate concertDate = concertDateRepository.save(ConcertDate.create(concert, LocalDate.of(2035, 12, 12)));
        Seat seat = createSeatWithConcert(concertDate, "A", "1", "01", "VIP");


        // 첫 번째 유저 예약
        MakeReservationResult firstReservation = reserveSeat(firstUser.getId(), seat.getId());

        // 좌석 만료 시뮬레이션
        reservationRepository.updateExpiration(firstReservation.reservationId(), firstReservation.tempHoldExpiresAt().minusMinutes(10));

        // 다른 유저 생성 후 예약
        User secondUser = createUser();
        MakeReservationResult secondReservation = reserveSeat(secondUser.getId(), seat.getId());

        // then
        assertThat(secondReservation).isNotNull();
        assertThat(secondReservation.userId()).isEqualTo(secondUser.getId());
        assertThat(secondReservation.status()).isEqualTo(ReservationStatus.TEMP_HOLD.name());
    }

    @Test
    @DisplayName("Concurrent Seat Requests → Only one user can succeed")
    void testConcurrentSeatRequestsSingleSuccess() throws InterruptedException, ExecutionException {
        // given
        Concert concert = Concert.builder()
                .title("test_title")
                .build();
        concert = concertRepository.save(concert);
        ConcertDate concertDate = concertDateRepository.save(ConcertDate.create(concert, LocalDate.of(2035, 12, 12)));
        Seat seat = createSeatWithConcert(concertDate, "A", "1", "01", "VIP");


        ExecutorService executor = Executors.newFixedThreadPool(5);

        Callable<MakeReservationResult> task = () -> {
            try {
                User concurrentUser = createUser();
                return reserveSeat(concurrentUser.getId(), seat.getId());
            } catch (Exception e) {
                return null;
            }
        };

        // when
        Future<MakeReservationResult>[] results = new Future[5];
        for (int i = 0; i < 5; i++) {
            results[i] = executor.submit(task);
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // then
        long successCount = 0;
        for (Future<MakeReservationResult> f : results) {
            if (f.get() != null) successCount++;
        }

        assertThat(successCount).isEqualTo(1);
    }
}
