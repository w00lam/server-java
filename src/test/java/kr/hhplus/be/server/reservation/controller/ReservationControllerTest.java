package kr.hhplus.be.server.reservation.controller;

import kr.hhplus.be.server.adapter.in.web.ReservationController;
import kr.hhplus.be.server.concert.domain.Seat;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.ReservationStatus;
import kr.hhplus.be.server.reservation.dto.ReservationRequest;
import kr.hhplus.be.server.reservation.dto.ReservationResponse;
import kr.hhplus.be.server.reservation.usecase.MakeReservationUseCase;
import kr.hhplus.be.server.user.domain.User;
import org.junit.jupiter.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2024, 1, 1, 12, 0);

    private User createUserFixture() {
        User user = new User();
        user.setId(FIXED_USER_ID);
        return user;
    }

    private Seat createSeatFixture() {
        Seat seat = new Seat();
        seat.setId(FIXED_SEAT_ID);
        return seat;
    }

    private Reservation createReservationFixture() {
        return new Reservation(
                FIXED_RESERVATION_ID,
                createUserFixture(),
                createSeatFixture(),
                ReservationStatus.TEMP_HOLD,
                FIXED_TIME,
                null,
                FIXED_TIME,
                FIXED_TIME,
                false
        );
    }

    @Test
    @DisplayName("좌석 예약 요청 시 ReservationResponse 반환")
    void makeReservation_returnsResponse() {
        // given
        Reservation reservation = createReservationFixture();
        when(useCase.makeReservation(FIXED_USER_ID, FIXED_SEAT_ID))
                .thenReturn(reservation);

        ReservationRequest request = new ReservationRequest();
        request.setUserId(FIXED_USER_ID);
        request.setSeatId(FIXED_SEAT_ID);

        // when
        ResponseEntity<ReservationResponse> response =
                controller.makeReservation(request);

        // then
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(FIXED_RESERVATION_ID, response.getBody().getReservationId());
        verify(useCase, times(1)).makeReservation(FIXED_USER_ID, FIXED_SEAT_ID);
    }
}
