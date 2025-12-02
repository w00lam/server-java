package kr.hhplus.be.server.application.payment.port.in;

public interface MakePaymentUseCase {
    MakePaymentResult execute(MakePaymentCommand command);
}
