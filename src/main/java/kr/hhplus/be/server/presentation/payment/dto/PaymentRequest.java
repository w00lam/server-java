package kr.hhplus.be.server.presentation.payment.dto;

import java.util.UUID;

public record PaymentRequest(UUID reservationId, int amount) {
}
