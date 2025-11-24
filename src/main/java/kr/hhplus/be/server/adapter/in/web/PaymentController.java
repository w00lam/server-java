package kr.hhplus.be.server.adapter.in.web;

import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.dto.PaymentRequest;
import kr.hhplus.be.server.payment.dto.PaymentResponse;
import kr.hhplus.be.server.payment.usecase.MakePaymentUseCase;
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
    private final MakePaymentUseCase useCase;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        Payment payment = useCase.execute(request.getReservationId(), request.getAmount());
        PaymentResponse response = new PaymentResponse(payment.getId(), payment.getReservationId(), payment.getAmount(), payment.getStatus().name());
        return ResponseEntity.ok(response);
    }
}
