package kr.hhplus.be.server.domain.payment.model;

import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.common.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest extends BaseUnitTest {
    @Test
    @DisplayName("createPending()은 결제 대기 상태의 Payment를 생성해야 한다")
    void createPending_shouldCreatePendingPayment() {
        // given
        Reservation reservation = Reservation.builder()
                .id(fixedUUID())
                .build();

        int amount = 40000;

        // when
        Payment payment = Payment.createPending(reservation, amount, PaymentMethod.CARD);

        // then
        assertNotNull(payment.getId(), "ID는 생성되어야 한다");
        assertEquals(reservation, payment.getReservation());
        assertEquals(amount, payment.getAmount());
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertNull(payment.getPaidAt());
        assertFalse(payment.isDeleted());

        // method는 createPending에서 설정하지 않으므로 null
        assertNull(payment.getMethod(), "method는 null이어야 한다");

        // JPA auditing은 단위 테스트에서는 동작하지 않음 → null
        assertNull(payment.getCreatedAt());
        assertNull(payment.getUpdatedAt());
    }

    @Test
    @DisplayName("Builder로 Payment를 생성하면 값이 모두 정상적으로 설정되어야 한다")
    void builder_shouldSetAllFieldsCorrectly() {
        // given
        Reservation reservation = Reservation.builder()
                .id(fixedUUID())
                .build();

        // when
        Payment payment = Payment.builder()
                .id(fixedUUID())
                .reservation(reservation)
                .amount(55000)
                .method(PaymentMethod.CARD)
                .status(PaymentStatus.PAID)
                .paidAt(fixedNow())
                .deleted(false)
                .build();

        // then
        assertEquals(fixedUUID(), payment.getId());
        assertEquals(reservation, payment.getReservation());
        assertEquals(55000, payment.getAmount());
        assertEquals(PaymentMethod.CARD, payment.getMethod());
        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertNotNull(payment.getPaidAt());
        assertFalse(payment.isDeleted());


        assertNull(payment.getCreatedAt());
        assertNull(payment.getUpdatedAt());
    }
}
