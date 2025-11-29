package kr.hhplus.be.server.payment.service;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.port.PaymentServicePort;
import kr.hhplus.be.server.payment.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentServicePort {
    private final PaymentRepository repository;
    private final Clock clock;

    @Override
    public Payment pay(UUID reservationId, int amount) {
        if (Objects.isNull(reservationId)) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Payment payment = Payment.createPending(reservationId, amount, clock);

        return repository.save(payment);
    }
}
