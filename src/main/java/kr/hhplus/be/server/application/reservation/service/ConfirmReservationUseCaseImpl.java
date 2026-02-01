package kr.hhplus.be.server.application.reservation.service;

import kr.hhplus.be.server.application.concert.service.GetConcertRankingService;
import kr.hhplus.be.server.application.event.DomainEventPublisher;
import kr.hhplus.be.server.application.reservation.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.application.reservation.port.in.ConfirmReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.ConfirmReservationResult;
import kr.hhplus.be.server.application.reservation.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfirmReservationUseCaseImpl implements ConfirmReservationUseCase {
    private final ReservationRepositoryPort reservationRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public ConfirmReservationResult execute(ConfirmReservationCommand command) {
        boolean success = reservationRepository.confirmIfNotExpired(command.reservationId());

        if (!success) {
            throw new IllegalStateException("Reservation expired or already processed");
        }

        var reservation = reservationRepository.findById(command.reservationId());

        eventPublisher.publish(
                new ReservationConfirmedEvent(
                        reservation.getId(),
                        reservation.getSeat().getConcertDate().getConcert().getId()
                )
        );

        return new ConfirmReservationResult(
                reservation.getId(),
                reservation.getStatus().name(),
                reservation.getConfirmedAt()
        );
    }
}
