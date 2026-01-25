package kr.hhplus.be.server.application.reservation.service;

import kr.hhplus.be.server.application.reservation.event.ReservationCanceledEvent;
import kr.hhplus.be.server.application.reservation.port.in.CancelReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.CancelReservationResult;
import kr.hhplus.be.server.application.reservation.port.in.CancelReservationUseCase;
import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CancelReservationUseCaseImpl implements CancelReservationUseCase {
    private final ReservationRepositoryPort reservationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public CancelReservationResult execute(CancelReservationCommand command) {
        Reservation reservation = reservationRepository.findById(command.reservationId());

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new IllegalStateException("Reservation already cancelled");
        }

        boolean wasConfirmed = reservation.getStatus() == ReservationStatus.CONFIRMED;

        reservation.cancel();
        reservationRepository.save(reservation);

        if (wasConfirmed) {
            eventPublisher.publishEvent(
                    new ReservationCanceledEvent(
                            reservation.getId(),
                            reservation.getSeat().getConcertDate().getConcert().getId()
                    )
            );
        }

        return new CancelReservationResult(
                reservation.getId(),
                reservation.getStatus().name(),
                reservation.getUpdatedAt()
        );
    }
}

