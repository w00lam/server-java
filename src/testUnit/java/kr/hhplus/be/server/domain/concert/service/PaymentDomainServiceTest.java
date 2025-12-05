package kr.hhplus.be.server.domain.concert.service;

import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.payment.service.PaymentDomainService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.test.unit.BaseUnitTest;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentDomainServiceTest extends BaseUnitTest {
    private final PaymentDomainService service = new PaymentDomainService();

    @Test
    @DisplayName("validateAmount: 음수나 0이면 예외 발생")
    void validateAmount_invalid() {
        assertThrows(IllegalArgumentException.class, () -> service.validateAmount(0));
        assertThrows(IllegalArgumentException.class, () -> service.validateAmount(-1));
    }

    @Test
    @DisplayName("validateAmount: 양수는 정상 처리")
    void validateAmount_valid() {
        assertDoesNotThrow(() -> service.validateAmount(100));
    }

    @Test
    @DisplayName("createPending: Payment 객체 정상 생성")
    void createPending_createsPayment() {
        Reservation reservation = Reservation.builder().id(fixedUUID()).build();
        Payment payment = service.createPending(reservation, 5000);

        assertNotNull(payment);
        assertEquals(5000, payment.getAmount());
        assertEquals(reservation, payment.getReservation());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertNull(payment.getPaidAt());
        assertFalse(payment.isDeleted());
    }
}
