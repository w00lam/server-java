package kr.hhplus.be.server.payment.service;

import kr.hhplus.be.server.payment.domain.Payment;

import java.util.UUID;

public interface PaymentService {
    Payment pay(UUID reservationId, int amount);
}
