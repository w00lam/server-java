package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentMethod;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import org.springframework.stereotype.Component;

@Component
public class PaymentDomainService {
    public void validateAmount(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than zero");
    }

    public Payment createPending(Reservation reservation, int amount, PaymentMethod method) {
        return Payment.createPending(reservation, amount, method);
    }
}
