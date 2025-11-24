package kr.hhplus.be.server.reservation.service;

import kr.hhplus.be.server.reservation.domain.DefaultReservationExpirationPolicy;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.ReservationExpirationPolicy;
import kr.hhplus.be.server.reservation.repository.ReservationRepository;
import kr.hhplus.be.server.reservation.usecase.MakeReservationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("좌석 예약 요청 UseCase 단위 테스트")
public class MakeReservationUseCaseTest {
    @Mock
    private ReservationRepository repository;

    @InjectMocks
    private MakeReservationUseCase useCase;

    private final ReservationExpirationPolicy expirationPolicy = new DefaultReservationExpirationPolicy();

    private static final UUID FIXED_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FIXED_SEAT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2025-12-12T00:00:00Z"), ZoneId.of("UTC"));

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Reflection을 이용해 private final Clock 필드 주입
        Field clockField = MakeReservationUseCase.class.getDeclaredField("clock");
        clockField.setAccessible(true);
        clockField.set(useCase, FIXED_CLOCK);

        // Reflection을 이용해 private final expirationPolicy 주입
        Field policyField = MakeReservationUseCase.class.getDeclaredField("expirationPolicy");
        policyField.setAccessible(true);
        policyField.set(useCase, expirationPolicy);
    }

    @Test
    @DisplayName("좌석 예약 요청 성공 시 TEMP_HOLD 상태로 생성")
    void makeReservation_success() {
        when(repository.findBySeatIdAndActive(FIXED_SEAT_ID)).thenReturn(Optional.empty());

        Reservation reservation = useCase.makeReservation(FIXED_USER_ID, FIXED_SEAT_ID);

        assertEquals(FIXED_USER_ID, reservation.getUserId());
        assertEquals(FIXED_SEAT_ID, reservation.getSeatId());
        assertEquals("TEMP_HOLD", reservation.getStatus().name());

        verify(repository, times(1)).save(reservation);
    }

    @Test
    @DisplayName("이미 예약된 좌석이면 예외 발생")
    void makeReservation_alreadyExists_throwsException() {
        Reservation existing = Reservation.create(FIXED_USER_ID, FIXED_SEAT_ID, FIXED_CLOCK, expirationPolicy);

        when(repository.findBySeatIdAndActive(FIXED_SEAT_ID)).thenReturn(Optional.of(existing));

        try {
            useCase.makeReservation(FIXED_USER_ID, FIXED_SEAT_ID);
        } catch (IllegalStateException e) {
            assertEquals("Seat Already reserved.", e.getMessage());
        }

        verify(repository, never()).save(any());
    }
}
