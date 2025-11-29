package kr.hhplus.be.server.payment.usecase;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.port.PaymentServicePort;
import kr.hhplus.be.server.payment.service.PaymentServiceImpl;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class MakePaymentUseCase {
    private final PaymentServicePort service;

    public Payment execute(UUID reservationId,int amount){
        return service.pay(reservationId, amount);
    }
}
