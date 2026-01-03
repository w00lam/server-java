package kr.hhplus.be.server.application.reservation.service;

import kr.hhplus.be.server.application.concert.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationResult;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationUseCase;
import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.application.user.port.out.UserRepositoryPort;
import kr.hhplus.be.server.domain.concert.model.Seat;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationExpirationPolicy;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class MakeReservationUseCaseImpl implements MakeReservationUseCase {
    private final ReservationRepositoryPort reservationRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final SeatRepositoryPort seatRepositoryPort;
    private final ReservationExpirationPolicy policy;
    private final Clock clock;

    @Override
    @Transactional
    public MakeReservationResult execute(MakeReservationCommand command) {
        User user = userRepositoryPort.findById(command.userId());
        Seat seat = seatRepositoryPort.findByIdForUpdate(command.seatId());

        if (reservationRepositoryPort.existsBySeatAndStatus(seat, ReservationStatus.TEMP_HOLD)
                || reservationRepositoryPort.existsBySeatAndStatus(seat, ReservationStatus.CONFIRMED)) {
            throw new IllegalStateException("Seat already reserved");
        }
        Reservation tempHold = Reservation.create(user, seat, clock, policy);
        reservationRepositoryPort.save(tempHold);

        return new MakeReservationResult(tempHold.getId(), tempHold.getUser().getId(), tempHold.getSeat().getId(), tempHold.getStatus().name(), tempHold.getTempHoldExpiresAt());
    }
}
