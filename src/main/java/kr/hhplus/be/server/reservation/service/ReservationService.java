package kr.hhplus.be.server.reservation.service;

import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.usecase.MakeReservationUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ReservationService {
    private final MakeReservationUseCase makeReservationUseCase;

    public Reservation reserveSeat(UUID userId, UUID seatId) {
        return makeReservationUseCase.makeReservation(userId, seatId);
    }
}
