package kr.hhplus.be.server.application.payment.port.out;

import kr.hhplus.be.server.domain.payment.model.Payment;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);
}
