package kr.hhplus.be.server.payment.controller;

import kr.hhplus.be.server.adapter.in.web.PaymentController;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.payment.dto.PaymentRequest;
import kr.hhplus.be.server.payment.dto.PaymentResponse;
import kr.hhplus.be.server.payment.usecase.MakePaymentUseCase;
import kr.hhplus.be.server.reservation.domain.Reservation;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentController 단위 테스트")
class PaymentControllerTest {

    @Mock
    private MakePaymentUseCase makePaymentUseCase;

    @InjectMocks
    private PaymentController controller;

    private static final UUID FIXED_PAYMENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FIXED_RESERVATION_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final int FIXED_AMOUNT = 15000;

    private Reservation createReservationFixture() {
        Reservation reservation = new Reservation();
        reservation.setId(FIXED_RESERVATION_ID);
        reservation.setCreatedAt(LocalDateTime.of(2025, 12, 12, 0, 0));
        reservation.setUpdatedAt(LocalDateTime.of(2025, 12, 12, 0, 0));
        reservation.setDeleted(false);
        return reservation;
    }

    private Payment createPaymentFixture(Reservation reservation) {
        return Payment.builder()
                .id(FIXED_PAYMENT_ID)
                .reservation(reservation)
                .amount(FIXED_AMOUNT)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.of(2025, 12, 12, 0, 0))
                .updatedAt(LocalDateTime.of(2025, 12, 12, 0, 0))
                .deleted(false)
                .build();
    }

    private PaymentRequest createPaymentRequestFixture() {
        return new PaymentRequest(FIXED_RESERVATION_ID, FIXED_AMOUNT);
    }

    @Test
    @DisplayName("POST /payments 요청 시 결제 생성 후 PENDING 상태 응답을 반환한다")
    void createPayment_returnsPendingResponse() throws Exception {

        Reservation reservation = createReservationFixture();
        Payment mockPayment = createPaymentFixture(reservation);
        PaymentRequest request = createPaymentRequestFixture();

        when(makePaymentUseCase.execute(reservation.getId(), anyInt())).thenReturn(mockPayment);

        ResponseEntity<PaymentResponse> response = controller.createPayment(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getReservationId()).isEqualTo(FIXED_RESERVATION_ID);
        assertThat(response.getBody().getAmount()).isEqualTo(FIXED_AMOUNT);
        assertThat(response.getBody().getStatus()).isEqualTo(PaymentStatus.PENDING.name());
    }
}
