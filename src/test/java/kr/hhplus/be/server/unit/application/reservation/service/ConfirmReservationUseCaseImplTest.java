package kr.hhplus.be.server.unit.application.reservation.service;

import kr.hhplus.be.server.application.concert.service.GetConcertRankingService;
import kr.hhplus.be.server.application.reservation.port.in.ConfirmReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.ConfirmReservationResult;
import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.application.reservation.service.ConfirmReservationUseCaseImpl;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertDate;
import kr.hhplus.be.server.domain.concert.model.seat.Seat;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ConfirmReservationUseCaseImplTest extends BaseUnitTest {
    @Mock
    ReservationRepositoryPort reservationRepository;
    @Mock
    GetConcertRankingService getConcertRankingService;

    @InjectMocks
    ConfirmReservationUseCaseImpl useCase;

    @Test
    void 예약이_확정되면_랭킹이_증가한다() {
        // given
        Concert concert = Concert.builder()
                .id(fixedUUID())
                .build();

        ConcertDate concertDate = ConcertDate.builder()
                .concert(concert)
                .build();

        Seat seat = Seat.builder()
                .concertDate(concertDate)
                .build();

        Reservation reservation = Reservation.builder()
                .id(fixedUUID2())
                .seat(seat)
                .status(ReservationStatus.TEMP_HOLD)
                .tempHoldExpiresAt(fixedNow().plusMinutes(10))
                .build();
        when(reservationRepository.confirmIfNotExpired(fixedUUID2())).thenReturn(true);
        when(reservationRepository.findById(fixedUUID2())).thenReturn(reservation);

        // when
        useCase.execute(new ConfirmReservationCommand(fixedUUID2()));

        // then
        verify(reservationRepository).confirmIfNotExpired(fixedUUID2());
        verify(getConcertRankingService).increaseReservation(fixedUUID());
    }

    @Test
    void 이미_확정된_예약이면_예외가_발생한다() {
        // given
        when(reservationRepository.confirmIfNotExpired(fixedUUID()))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> useCase.execute(new ConfirmReservationCommand(fixedUUID())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Reservation expired or already processed");

        verify(getConcertRankingService, never()).increaseReservation(any());
    }
}
