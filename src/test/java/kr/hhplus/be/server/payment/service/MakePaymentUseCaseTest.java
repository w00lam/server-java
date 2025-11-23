package kr.hhplus.be.server.payment.service;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.payment.usecase.MakePaymentUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MakePaymentUseCaseTest 단위 테스트")
public class MakePaymentUseCaseTest {
    @Mock
    private PaymentServiceImpl service;

    @InjectMocks
    private MakePaymentUseCase useCase;

    @Test
    @DisplayName("결제 요청 시 PENDING 상태 Payment 반환")
    void pay_returnsPendingPayment() {
        // given
        UUID reservationId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        int amount = 10000;

        Payment mockPayment = new Payment(
                UUID.randomUUID(),
                reservationId,
                amount,
                PaymentStatus.PENDING,
                null,
                null,
                null,
                false
        );

        when(service.pay(reservationId, amount)).thenReturn(mockPayment);

        // when
        Payment result = useCase.execute(reservationId, amount);

        // then
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(result.getReservationId()).isEqualTo(reservationId);
        assertThat(result.getAmount()).isEqualTo(amount);
    }
}
