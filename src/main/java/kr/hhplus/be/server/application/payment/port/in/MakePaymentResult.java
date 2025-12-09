package kr.hhplus.be.server.application.payment.port.in;

import java.util.UUID;

public record MakePaymentResult(UUID paymentId,String status) {
}
