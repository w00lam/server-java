package kr.hhplus.be.server.reservation.service;

import kr.hhplus.be.server.reservation.domain.Reservation;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ReservationService {
    public Reservation reserveSeat(UUID userId, UUID seatId);
}
