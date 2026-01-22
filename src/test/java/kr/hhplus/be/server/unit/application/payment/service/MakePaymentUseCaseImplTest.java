package kr.hhplus.be.server.unit.application.payment.service;

import kr.hhplus.be.server.application.payment.port.in.MakePaymentCommand;
import kr.hhplus.be.server.application.payment.port.in.MakePaymentResult;
import kr.hhplus.be.server.application.payment.port.out.PaymentRepositoryPort;
import kr.hhplus.be.server.application.payment.service.MakePaymentUseCaseImpl;
import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentMethod;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.payment.service.PaymentDomainService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MakePaymentUseCaseImplTest extends BaseUnitTest {
    @Mock
    ReservationRepositoryPort reservationRepositoryPort;

    @Mock
    PaymentRepositoryPort paymentRepositoryPort;

    @Mock
    PaymentDomainService paymentDomainService;

    @InjectMocks
    MakePaymentUseCaseImpl useCase;

    @Test
    @DisplayName("결제 요청 시 예약 조회 → 금액 검증 → Pending 생성 → 저장 후 결과 반환")
    void execute_success() {
        // given
        UUID reservationId = fixedUUID();
        int amount = 10000;

        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount, PaymentMethod.CARD);

        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .build();

        Payment pendingPayment = Payment.builder()
                .id(fixedUUID())
                .status(PaymentStatus.PENDING)
                .amount(amount)
                .reservation(reservation)
                .build();

        Payment savedPayment = Payment.builder()
                .id(fixedUUID())   // 결과 검증을 위해 고정 UUID 사용
                .status(PaymentStatus.PENDING)
                .amount(amount)
                .reservation(reservation)
                .build();

        when(reservationRepositoryPort.findById(reservationId)).thenReturn(reservation);
        doNothing().when(paymentDomainService).validateAmount(amount);
        when(paymentDomainService.createPending(reservation, amount, PaymentMethod.CASH)).thenReturn(pendingPayment);
        when(paymentRepositoryPort.save(pendingPayment)).thenReturn(savedPayment);

        // when
        MakePaymentResult result = useCase.execute(command);

        // then
        assertEquals(savedPayment.getId(), result.paymentId());
        assertEquals(PaymentStatus.PENDING.name(), result.status());

        verify(reservationRepositoryPort).findById(reservationId);
        verify(paymentDomainService).validateAmount(amount);
        verify(paymentDomainService).createPending(reservation, amount, PaymentMethod.CASH);
        verify(paymentRepositoryPort).save(pendingPayment);
    }
}
