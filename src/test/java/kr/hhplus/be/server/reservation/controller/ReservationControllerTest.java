package kr.hhplus.be.server.reservation.controller;

import kr.hhplus.be.server.adapter.in.web.ReservationController;
import kr.hhplus.be.server.reservation.entity.Reservation;
import kr.hhplus.be.server.reservation.entity.ReservationStatus;
import kr.hhplus.be.server.reservation.dto.ReservationRequest;
import kr.hhplus.be.server.reservation.dto.ReservationResponse;
import kr.hhplus.be.server.reservation.usecase.MakeReservationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationController 단위 테스트")
public class ReservationControllerTest {
    @Mock
    private MakeReservationUseCase useCase;

    @InjectMocks
    private ReservationController controller;

    private static final UUID FIXED_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FIXED_SEAT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID FIXED_RESERVATION_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Test
    @DisplayName("좌석 예약 요청 시 ReservationResponse 반환")
    void makeReservation_returnsResponse() {
        Reservation reservation = new Reservation(
                FIXED_RESERVATION_ID,
                FIXED_USER_ID,
                FIXED_SEAT_ID,
                ReservationStatus.TEMP_HOLD,
                LocalDateTime.now(),
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false
        );

        when(useCase.makeReservation(FIXED_USER_ID, FIXED_SEAT_ID)).thenReturn(reservation);

        ReservationRequest request = new ReservationRequest();
        request.setUserId(FIXED_USER_ID);
        request.setSeatId(FIXED_SEAT_ID);

        ResponseEntity<ReservationResponse> response = controller.makeReservation(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(FIXED_RESERVATION_ID, (response.getBody()).getReservationId());
        verify(useCase, times(1)).makeReservation(FIXED_USER_ID, FIXED_SEAT_ID);
    }
}
