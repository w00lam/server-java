package kr.hhplus.be.server.intergration.reservation;

import kr.hhplus.be.server.application.point.port.in.DeductPointCommand;
import kr.hhplus.be.server.application.point.port.in.DeductPointUseCase;
import kr.hhplus.be.server.application.reservation.port.in.ConfirmReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationUseCase;
import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.application.user.port.out.UserRepositoryPort;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.seat.Seat;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.intergration.ReservationIntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ReservationPaymentConcurrencyTest extends ReservationIntegrationTestBase {
    @Autowired
    MakeReservationUseCase makeReservationUseCase;
    @Autowired
    ConfirmReservationUseCase confirmReservationUseCase;
    @Autowired
    DeductPointUseCase deductPointUseCase;
    @Autowired
    ReservationRepositoryPort reservationRepository;
    @Autowired
    UserRepositoryPort userRepository;

    @Test
    @DisplayName("동일 좌석 예약 + 결제 동시 요청 시 단 1건만 성공한다")
    void reserve_and_pay_concurrently() throws Exception {
        // given
        User user1 = createUserWithPoints(1000);
        User user2 = createUserWithPoints(1000);
        User user3 = createUserWithPoints(1000);

        Concert concert = Concert.builder()
                .title("concert")
                .build();

        Seat seat = createSeat();
        UUID seatId = seat.getId();

        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        List<User> users = List.of(user1, user2, user3);

        // when
        for (User user : users) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // 🔥 동시 시작 보장

                    // 1. 임시 예약
                    var reserveResult = makeReservationUseCase.execute(
                            new MakeReservationCommand(user.getId(), concert.getId(), seatId)
                    );

                    // 2. 확정
                    confirmReservationUseCase.execute(
                            new ConfirmReservationCommand(reserveResult.reservationId())
                    );

                    // 3. 결제
                    deductPointUseCase.execute(
                            new DeductPointCommand(user.getId(), 500)
                    );

                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // 🚀 동시에 출발
        doneLatch.await();
        executor.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(2);

        long confirmedCount = reservationRepository.countBySeatAndStatus(seat, ReservationStatus.CONFIRMED);

        assertThat(confirmedCount).isEqualTo(1);
    }
}
