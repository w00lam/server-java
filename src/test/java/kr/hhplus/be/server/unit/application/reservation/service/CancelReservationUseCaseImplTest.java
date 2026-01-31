package kr.hhplus.be.server.unit.application.reservation.service;

import kr.hhplus.be.server.application.concert.service.GetConcertRankingService;
import kr.hhplus.be.server.application.reservation.port.in.CancelReservationCommand;
import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.application.reservation.service.CancelReservationUseCaseImpl;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertDate;
import kr.hhplus.be.server.domain.concert.model.seat.Seat;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CancelReservationUseCaseImplTest extends BaseUnitTest {
    @Mock
    ReservationRepositoryPort reservationRepository;
    @Mock
    GetConcertRankingService getConcertRankingService;

    @InjectMocks
    CancelReservationUseCaseImpl useCase;

    @Test
    void 확정된_예약을_취소하면_상태가_CANCELLED로_변경되고_랭킹이_감소한다() {
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
                .status(ReservationStatus.CONFIRMED)
                .build();

        when(reservationRepository.findById(fixedUUID2())).thenReturn(reservation);

        // when
        useCase.execute(new CancelReservationCommand(fixedUUID2()));

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);

        verify(getConcertRankingService).decreaseReservation(fixedUUID());
        verify(reservationRepository).save(reservation);
    }


    @Test
    void 확정되지_않은_예약을_취소하면_랭킹은_감소하지_않는다() {
        // given
        Reservation reservation = Reservation.builder()
                .id(fixedUUID())
                .status(ReservationStatus.TEMP_HOLD)
                .build();

        when(reservationRepository.findById(fixedUUID())).thenReturn(reservation);

        // when
        useCase.execute(new CancelReservationCommand(fixedUUID()));

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);

        verify(getConcertRankingService, never()).decreaseReservation(any());
        verify(reservationRepository).save(reservation);
    }
}
