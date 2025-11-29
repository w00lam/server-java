package kr.hhplus.be.server.reservation.service;

import kr.hhplus.be.server.concert.domain.Seat;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.ReservationExpirationPolicy;
import kr.hhplus.be.server.reservation.repository.ReservationRepository;
import kr.hhplus.be.server.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService 단위 테스트")
public class ReservationServiceImplTest {

    @Mock
    private ReservationRepository repository;

    @Mock
    private Clock clock;

    @Mock
    private ReservationExpirationPolicy expirationPolicy;

    @InjectMocks
    private ReservationServiceImpl service;

    private User user;
    private Seat seat;

    private final Instant fixedInstant = Instant.parse("2025-11-29T00:00:00Z");

    private static final UUID FIXED_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final UUID FIXED_SEAT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUp() {
        // Clock 시간 고정
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        user = new User();
        user.setId(FIXED_USER_ID);

        seat = new Seat();
        seat.setId(FIXED_SEAT_ID);
    }

    @Test
    @DisplayName("좌석 예약 성공 - 기존 활성 예약이 없으면 새로운 예약 생성")
    void reserveSeat_successful() {
        when(repository.findBySeatIdAndActive(FIXED_SEAT_ID)).thenReturn(Optional.empty());

        when(repository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        Reservation reservation = service.reserveSeat(user, seat);

        assertNotNull(reservation);
        assertEquals(FIXED_USER_ID, reservation.getUser().getId());
        assertEquals(FIXED_SEAT_ID, reservation.getSeat().getId());

        verify(repository, times(1)).findBySeatIdAndActive(FIXED_SEAT_ID);
        verify(repository, times(1)).save(reservation);
    }
}
