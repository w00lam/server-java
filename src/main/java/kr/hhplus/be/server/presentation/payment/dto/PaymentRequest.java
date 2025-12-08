package kr.hhplus.be.server.presentation.payment.dto;

import kr.hhplus.be.server.domain.payment.model.PaymentMethod;

import java.util.UUID;

public record PaymentRequest(UUID reservationId, int amount, PaymentMethod method) {
}
