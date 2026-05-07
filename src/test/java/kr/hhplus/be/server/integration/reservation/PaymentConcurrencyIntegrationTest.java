package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PaymentConcurrencyIntegrationTest extends ReservationIntegrationTestBase {
    @Test
    void concurrentSamePaymentRequest_createsOnePaymentAndDeductsPointsOnce() throws Exception {
        User user = createUserWithPoints(10_000);
        UUID reservationId = createReservation(user);
        MakePaymentCommand command = new MakePaymentCommand(reservationId, 5_000, PaymentMethod.CARD);

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        ConcurrentLinkedQueue<UUID> successfulPaymentIds = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Throwable> unexpectedFailures = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    successfulPaymentIds.add(makePaymentUseCase.execute(command).paymentId());
                } catch (BusinessRuleViolationException expectedRaceLoss) {
                    // Another transaction may confirm the reservation first.
                } catch (Exception exception) {
                    unexpectedFailures.add(exception);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        em.clear();

        assertThat(unexpectedFailures).isEmpty();
        assertThat(successfulPaymentIds).isNotEmpty();
        assertThat(successfulPaymentIds.stream().distinct()).hasSize(1);
        assertThat(countPaymentsByReservationId(reservationId)).isEqualTo(1);
        assertThat(userRepository.findById(user.getId()).getPoints()).isEqualTo(5_000);
        assertThat(reservationRepository.findById(reservationId).getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    private UUID createReservation(User user) {
        Concert concert = concertRepository.save(
                Concert.builder()
                        .title("payment concurrency concert")
                        .build()
        );
        ConcertDate concertDate = concertDateRepository.save(
                ConcertDate.create(concert, LocalDate.now())
        );
        Seat seat = createSeatWithConcert(concertDate, "A", "1", "1", "VIP");

        return reserveSeat(user.getId(), concert.getId(), seat.getId()).reservationId();
    }
}
