package kr.hhplus.be.server.reservation.service;

import kr.hhplus.be.server.reservation.usecase.MakeReservationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService 단위 테스트")
public class ReservationServiceTest {
    @Mock
    MakeReservationUseCase useCase;

    @InjectMocks
    ReservationService service;

    private static final UUID FIXED_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FIXED_SEAT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Test
    @DisplayName("좌석 예약 요청 시 UseCase에서 이미 예약된 경우 예외 발생")
    void reserveSeat_whenSeatAlreadyReserved_throwsException() {
        when(useCase.makeReservation(FIXED_USER_ID, FIXED_SEAT_ID)).thenThrow(new IllegalStateException("Seat Already reserved."));

        assertThrows(IllegalStateException.class, () -> service.reserveSeat(FIXED_USER_ID, FIXED_SEAT_ID));
    }
}
