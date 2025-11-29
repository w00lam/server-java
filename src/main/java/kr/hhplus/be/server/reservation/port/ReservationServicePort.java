package kr.hhplus.be.server.reservation.port;

import kr.hhplus.be.server.concert.domain.Seat;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.user.domain.User;

import java.util.UUID;

public interface ReservationServicePort {
    Reservation reserveSeat(User user, Seat seat);
}
