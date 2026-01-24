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
    private final ReservationTxService reservationTxService;
    private final DistributedLockManager lockManager;


    @Override
    public MakeReservationResult execute(MakeReservationCommand command) {
        String lockKey = SeatLockKey.of(command.concertId(), command.seatId());
        String lockValue = lockManager.lock(lockKey, Duration.ofSeconds(5));


        if (lockValue == null) {
            throw new RuntimeException("Seat is already being reserved");
        }


        try {
            return reservationTxService.reserve(command);
        } finally {
            lockManager.unlock(lockKey, lockValue);
        }
    }
}
