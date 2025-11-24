package kr.hhplus.be.server.payment.repository;

import kr.hhplus.be.server.payment.domain.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
}
