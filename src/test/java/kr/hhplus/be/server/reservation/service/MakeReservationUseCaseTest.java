package kr.hhplus.be.server.reservation.service;

import kr.hhplus.be.server.concert.domain.Seat;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.port.ReservationServicePort;
import kr.hhplus.be.server.reservation.usecase.MakeReservationUseCase;
import kr.hhplus.be.server.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("좌석 예약 요청 UseCase 단위 테스트")
public class MakeReservationUseCaseTest {

    @Mock
    private ReservationServicePort servicePort;

    @InjectMocks
    private MakeReservationUseCase useCase;

    private final User user = new User();
    private final Seat seat = new Seat();

    private static final UUID FIXED_USER_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final UUID FIXED_SEAT_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    private static final UUID FIXED_RESERVATION_ID =
            UUID.fromString("33333333-3333-3333-3333-333333333333");

    private static final LocalDateTime FIXED_TIME =
            LocalDateTime.of(2025, 12, 12, 0, 0);

    private Reservation createReservationFixture() {
        Reservation reservation = new Reservation();
        reservation.setId(FIXED_RESERVATION_ID);
        reservation.setUser(user);
        reservation.setSeat(seat);
        reservation.setCreatedAt(FIXED_TIME);
        return reservation;
    }

    @Test
    @DisplayName("좌석 예약 성공 시 Reservation 반환")
    void makeReservation_successful() {
        Reservation expected = createReservationFixture();

        when(servicePort.reserveSeat(user, seat))
                .thenReturn(expected);

        Reservation actual = useCase.makeReservation(FIXED_USER_ID, FIXED_SEAT_ID);

        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(servicePort, times(1))
                .reserveSeat(user, seat);
    }
}
