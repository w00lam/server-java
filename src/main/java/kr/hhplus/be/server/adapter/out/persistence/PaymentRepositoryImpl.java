package kr.hhplus.be.server.adapter.out.persistence;

import kr.hhplus.be.server.adapter.out.persistence.jpa.JpaPaymentRepository;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final JpaPaymentRepository repository;

    @Override
    public Payment save(Payment payment) {
        return repository.save(payment);
    }
}
