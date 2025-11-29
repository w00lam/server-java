package kr.hhplus.be.server.payment.service;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.payment.repository.PaymentRepository;
import kr.hhplus.be.server.reservation.domain.Reservation;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentServiceImpl 단위 테스트")
public class PaymentServiceImplTest {
    @Mock
    private PaymentRepository repository;

    @Mock
    private Clock clock;

    @InjectMocks
    private PaymentServiceImpl service;

    private static final UUID FIXED_RESERVATION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final int FIXED_AMOUNT = 10000;
    private static final Instant FIXED_INSTANT = Instant.parse("2025-11-29T00:00:00Z");

    private Reservation createReservationFixture() {
        Reservation reservation = new Reservation();
        reservation.setId(FIXED_RESERVATION_ID);
        return reservation;
    }

    @Test
    @DisplayName("결제요청을 하면 Payment가 PENDING 상태로 생성된다")
    void whenRequestingPayment_thenPaymentIsCreatedInPendingState() {
        Reservation reservation = createReservationFixture();
        LocalDateTime fixedTime = LocalDateTime.ofInstant(FIXED_INSTANT, ZoneId.of("UTC"));

        when(repository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = service.pay(reservation.getId(), FIXED_AMOUNT);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getAmount()).isEqualTo(FIXED_AMOUNT);
        assertThat(payment.getReservation().getId()).isEqualTo(FIXED_RESERVATION_ID);
        assertThat(payment.getCreatedAt()).isEqualTo(fixedTime);
        assertThat(payment.getUpdatedAt()).isEqualTo(fixedTime);

        verify(repository, times(1)).save(any(Payment.class));
    }
}
