package kr.hhplus.be.server.payment.service;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.payment.repository.PaymentRepository;
import kr.hhplus.be.server.reservation.usecase.MakeReservationUseCase;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService 단위 테스트")
public class PaymentServiceTest {
    private PaymentRepository repository;
    private PaymentService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(PaymentRepository.class);
        Clock fixedClock = Clock.fixed(Instant.parse("2025-11-22T00:00:00Z"), ZoneId.of("UTC"));
        service = new PaymentServiceImpl(repository, fixedClock);
    }

    @Test
    @DisplayName("결제요청을 하면 Payment가 PENDING 상태로 생성된다")
    void whenRequestingPayment_thenPaymentIsCreatedInPendingState() {
        UUID reservationId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        int amount = 10000;

        when(repository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = service.pay(reservationId, amount);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getReservationId()).isEqualTo(reservationId);
        assertThat(payment.getCreatedAt()).isEqualTo(payment.getUpdatedAt());
    }
}
