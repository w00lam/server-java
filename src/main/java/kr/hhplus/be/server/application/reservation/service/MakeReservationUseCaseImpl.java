package kr.hhplus.be.server.application.reservation.service;

import kr.hhplus.be.server.application.concert.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationCommand;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationResult;
import kr.hhplus.be.server.application.reservation.port.in.MakeReservationUseCase;
import kr.hhplus.be.server.application.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.application.user.port.out.UserRepositoryPort;
import kr.hhplus.be.server.domain.concert.model.seat.Seat;
import kr.hhplus.be.server.domain.concert.model.seat.SeatLockKey;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationExpirationPolicy;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.lock.DistributedLockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MakeReservationUseCaseImpl implements MakeReservationUseCase {
    private final ReservationRepositoryPort reservationRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final SeatRepositoryPort seatRepositoryPort;
    private final DistributedLockManager lockManager;

    private final ReservationExpirationPolicy policy;
    private final Clock clock;

    @Override
    @Transactional
    public MakeReservationResult execute(MakeReservationCommand command) {
        String lockKey = SeatLockKey.of(command.concertId(), command.seatId());
        String lockValue = lockManager.lock(lockKey, Duration.ofSeconds(5));

        if (lockValue == null) {
            throw new RuntimeException("Seat is already being reserved");
        }

        try {
            User user = userRepositoryPort.findById(command.userId());
            Seat seat = seatRepositoryPort.findById(command.seatId());

            if (reservationRepositoryPort.existsBySeatAndStatus(seat, ReservationStatus.EXPIRED)
                    || reservationRepositoryPort.existsBySeatAndStatus(seat, ReservationStatus.CONFIRMED)) {
                throw new RuntimeException("Seat is already being reserved");
            }
            Reservation tempHold = Reservation.create(user, seat, clock, policy);
            reservationRepositoryPort.save(tempHold);

            return new MakeReservationResult(
                    tempHold.getId(),
                    user.getId(),
                    seat.getId(),
                    tempHold.getStatus().name(),
                    tempHold.getTempHoldExpiresAt()
            );
        } finally {
            lockManager.unlock(lockKey, lockValue);
        }
    }
}
