package kr.hhplus.be.server.reservation.service;

import kr.hhplus.be.server.concert.domain.Seat;
import kr.hhplus.be.server.reservation.domain.Reservation;
import kr.hhplus.be.server.reservation.domain.ReservationExpirationPolicy;
import kr.hhplus.be.server.reservation.port.ReservationServicePort;
import kr.hhplus.be.server.reservation.repository.ReservationRepository;
import kr.hhplus.be.server.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationServicePort {
    private final ReservationRepository repository;
    private final Clock clock;
    private final ReservationExpirationPolicy expirationPolicy;

    @Override
    public Reservation reserveSeat(User user, Seat seat) {
        // 활성 예약이 존재하는지 체크
        Optional<Reservation> activeReservation = repository.findBySeatIdAndActive(seat.getId());
        if (activeReservation.isPresent()) {
            throw new IllegalStateException("Seat Already reserved.");
        }

        // Reservation 생성 (팩토리 메서드)
        Reservation reservation = Reservation.create(user, seat, clock, expirationPolicy);

        // 저장
        repository.save(reservation);

        // 반환
        return reservation;
    }
}
