package kr.hhplus.be.server.application.payment.port.in;

import java.util.UUID;

public record MakePaymentCommand(UUID reservationId, int amount) {
}
