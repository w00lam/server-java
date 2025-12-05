package kr.hhplus.be.server.application.reservation.service;

import kr.hhplus.be.server.application.concert.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationResult;
import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.application.user.port.out.UserRepositoryPort;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationExpirationPolicy;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.test.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MakeReservationUseCaseImplTest extends BaseUnitTest {
    @Mock
    ReservationRepositoryPort reservationRepositoryPort;

    @Mock
    UserRepositoryPort userRepositoryPort;

    @Mock
    SeatRepositoryPort seatRepositoryPort;

    @Mock
    ReservationExpirationPolicy policy;

    @Mock
    Clock clock;

    @InjectMocks
    MakeReservationUseCaseImpl useCase;

    @Test
    @DisplayName("사용자와 좌석 정보로 임시 예약 생성 후 저장한다")
    void execute_success() {
        // given
        User user = User.builder().id(fixedUUID()).email("test@test.com").name("Tester").build();
        Seat seat = Seat.builder().id(fixedUUID()).section("A").row("1").number("1").grade("VIP").build();

        MakeReservationCommand command = new MakeReservationCommand(user.getId(), seat.getId());

        LocalDateTime now = fixedNow();
        LocalDateTime expiresAt = now.plusMinutes(5);

        when(userRepositoryPort.findById(user.getId())).thenReturn(user);
        when(seatRepositoryPort.findById(seat.getId())).thenReturn(seat);
        when(clock.instant()).thenReturn(now.toInstant(ZoneOffset.UTC));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        when(policy.expiresAt(now)).thenReturn(expiresAt);

        // when
        MakeReservationResult result = useCase.execute(command);

        // then
        assertEquals(user.getId(), result.userId());
        assertEquals(seat.getId(), result.seatId());
        assertEquals(ReservationStatus.TEMP_HOLD.name(), result.status());
        assertEquals(expiresAt, result.tempHoldExpiresAt());

        verify(reservationRepositoryPort).save(any(Reservation.class));
        verify(userRepositoryPort).findById(user.getId());
        verify(seatRepositoryPort).findById(seat.getId());
        verify(policy).expiresAt(now);
    }
}
