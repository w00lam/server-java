package kr.hhplus.be.server.application.payment.port.in;

import kr.hhplus.be.server.domain.payment.model.PaymentMethod;

import java.util.UUID;

public record MakePaymentCommand(UUID reservationId, int amount, PaymentMethod method) {
}
