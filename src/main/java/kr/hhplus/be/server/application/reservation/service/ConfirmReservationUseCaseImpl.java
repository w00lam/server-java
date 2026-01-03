package kr.hhplus.be.server.application.reservation.service;

import kr.hhplus.be.server.application.reservation.port.in.ConfirmReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.ConfirmReservationResult;
import kr.hhplus.be.server.application.reservation.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfirmReservationUseCaseImpl implements ConfirmReservationUseCase {
    private final ReservationRepositoryPort reservationRepository;

    @Override
    @Transactional
    public ConfirmReservationResult execute(ConfirmReservationCommand command) {
        boolean success = reservationRepository.confirmIfNotExpired(command.reservationId());

        if (!success) {
            throw new IllegalStateException("Reservation expired or already processed");
        }

        // 필요 시 재조회 (확정 결과 반환용)
        var reservation = reservationRepository.findById(command.reservationId());

        return new ConfirmReservationResult(
                reservation.getId(),
                reservation.getStatus().name(),
                reservation.getConfirmedAt()
        );
    }
}
