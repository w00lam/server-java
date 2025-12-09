package kr.hhplus.be.server.presentation.payment.controller;

import kr.hhplus.be.server.application.payment.port.in.MakePaymentCommand;
import kr.hhplus.be.server.application.payment.port.in.MakePaymentUseCase;
import kr.hhplus.be.server.presentation.payment.dto.PaymentRequest;
import kr.hhplus.be.server.presentation.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final MakePaymentUseCase makePaymentUseCase;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        var result = makePaymentUseCase.execute(new MakePaymentCommand(request.reservationId(), request.amount(), request.method()));
        var response = PaymentResponse.from(result);
        return ResponseEntity.ok(response);
    }
}
