package kr.hhplus.be.server.payment.port;

import kr.hhplus.be.server.payment.domain.Payment;

import java.util.UUID;

public interface PaymentServicePort {
    Payment pay(UUID reservationId, int amount);
}
