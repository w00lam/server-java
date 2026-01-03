package kr.hhplus.be.server.reservation;

import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertDate;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.persistence.jpa.JpaReservationRepository;
import kr.hhplus.be.server.reservation.common.ReservationIntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class SeatTempHoldConcurrencyTest extends ReservationIntegrationTestBase {
    @Autowired
    JpaReservationRepository jpaReservationRepository;

    @Test
    @DisplayName("동일 좌석에 대해 동시에 예약 요청 시 임시 배정은 1건만 성공한다")
    void reserve_seat_concurrently() throws Exception {
        // given
        User user1 = createUser();
        User user2 = createUser();
        User user3 = createUser();

        ConcertDate concertDate = concertDateRepository.save(
                ConcertDate.create(
                        concertRepository.save(Concert.builder()
                                .title("concert")
                                .build()),
                        LocalDate.now()
                )
        );

        Seat seat = createSeatWithConcert(concertDate, "A", "1", "1", "VIP");
        UUID seatId = seat.getId();

        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        executor.submit(() -> {
            try {
                reserveSeat(user1.getId(), seatId);
            } catch (Exception ignored) {
            } finally {
                latch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                reserveSeat(user2.getId(), seatId);
            } catch (Exception ignored) {
            } finally {
                latch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                reserveSeat(user3.getId(), seatId);
            } catch (Exception ignored) {
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        executor.shutdown();

        // then
        List<Reservation> reservations = jpaReservationRepository.findAll();

        long tempHoldCount = reservations.stream()
                .filter(r -> r.getSeat().getId().equals(seatId))
                .filter(r -> r.getStatus() == ReservationStatus.TEMP_HOLD)
                .count();

        assertThat(tempHoldCount).isEqualTo(1);
    }
}
