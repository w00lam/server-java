package kr.hhplus.be.server.payment.service;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.payment.port.PaymentServicePort;
import kr.hhplus.be.server.payment.usecase.MakePaymentUseCase;
import kr.hhplus.be.server.reservation.domain.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MakePaymentUseCaseTest 단위 테스트")
public class MakePaymentUseCaseTest {
    @Mock
    private PaymentServicePort service;

    @InjectMocks
    private MakePaymentUseCase useCase;

    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 12, 12, 10, 0);
    private static final UUID FIXED_RESERVATION_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final int FIXED_AMOUNT = 10000;

    private Reservation createReservationFixture() {
        Reservation reservation = new Reservation();
        reservation.setId(FIXED_RESERVATION_ID);
        reservation.setCreatedAt(FIXED_TIME);
        reservation.setUpdatedAt(FIXED_TIME);
        reservation.setDeleted(false);
        return reservation;
    }

    private Payment createPaymentFixture(Reservation reservation) {
        return Payment.builder()
                .id(UUID.randomUUID())
                .reservation(reservation)
                .amount(FIXED_AMOUNT)
                .status(PaymentStatus.PENDING)
                .createdAt(FIXED_TIME)
                .updatedAt(FIXED_TIME)
                .deleted(false)
                .build();
    }

    @Test
    @DisplayName("결제 요청 시 PENDING 상태 Payment 반환")
    void pay_returnsPendingPayment() {
        Reservation reservation = createReservationFixture();
        Payment mockPayment = createPaymentFixture(reservation);

        when(service.pay(reservation.getId(), FIXED_AMOUNT)).thenReturn(mockPayment);

        Payment result = useCase.execute(reservation.getId(), FIXED_AMOUNT);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.getReservation().getId()).isEqualTo(reservation.getId());
        assertThat(result.getAmount()).isEqualTo(FIXED_AMOUNT);
    }
}
