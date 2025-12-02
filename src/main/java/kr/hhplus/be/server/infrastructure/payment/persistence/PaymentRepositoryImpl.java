package kr.hhplus.be.server.infrastructure.payment.persistence;

import kr.hhplus.be.server.application.payment.port.out.PaymentRepositoryPort;
import kr.hhplus.be.server.infrastructure.payment.persistence.jpa.JpaPaymentRepository;
import kr.hhplus.be.server.domain.payment.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryPort {
    private final JpaPaymentRepository jpa;

    @Override
    public Payment save(Payment payment) {
        return jpa.save(payment);
    }
}
