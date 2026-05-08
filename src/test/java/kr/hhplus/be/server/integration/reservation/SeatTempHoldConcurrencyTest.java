package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.integration.support.ConcurrencyTestSupport;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.reservation.infrastructure.persistence.JpaReservationRepository;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
        Concert concert = Concert.builder()
                .title("concert")
                .build();

        ConcertDate concertDate = concertDateRepository.save(
                ConcertDate.create(
                        concertRepository.save(concert),
                        LocalDate.now()
                )
        );

        Seat seat = createSeatWithConcert(concertDate, "A", "1", "1", "VIP");
        UUID seatId = seat.getId();
        List<User> users = List.of(user1, user2, user3);

        int threadCount = users.size();

        // when
        var result = ConcurrencyTestSupport.runConcurrently(threadCount, index -> {
            try {
                reserveSeat(users.get(index).getId(), concert.getId(), seatId);
                return true;
            } catch (Exception exception) {
                return false;
            }
        });

        // then
        List<Reservation> reservations = jpaReservationRepository.findAll();

        long tempHoldCount = reservations.stream()
                .filter(r -> r.getSeat().getId().equals(seatId))
                .filter(r -> r.getStatus() == ReservationStatus.TEMP_HOLD)
                .count();

        assertThat(result.failures()).isEmpty();
        assertThat(result.successes()).containsExactlyInAnyOrder(true, false, false);
        assertThat(tempHoldCount).isEqualTo(1);
    }
}
