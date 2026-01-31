package kr.hhplus.be.server.application.payment.service;

import kr.hhplus.be.server.application.concert.service.GetConcertRankingService;
import kr.hhplus.be.server.application.payment.port.in.MakePaymentCommand;
import kr.hhplus.be.server.application.payment.port.in.MakePaymentResult;
import kr.hhplus.be.server.application.payment.port.in.MakePaymentUseCase;
import kr.hhplus.be.server.application.payment.port.out.PaymentRepositoryPort;
import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.service.PaymentDomainService;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MakePaymentUseCaseImpl implements MakePaymentUseCase {
    private final ReservationRepositoryPort reservationRepositoryPort;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentDomainService paymentDomainService;

    @Override
    public MakePaymentResult execute(MakePaymentCommand command) {
        Reservation reservation = reservationRepositoryPort.findById(command.reservationId());

        paymentDomainService.validateAmount(command.amount());

        Payment payment = paymentDomainService.createPending(reservation, command.amount(), command.method());

        Payment saved = paymentRepositoryPort.save(payment);

        return new MakePaymentResult(saved.getId(), saved.getStatus().name());
    }
}
