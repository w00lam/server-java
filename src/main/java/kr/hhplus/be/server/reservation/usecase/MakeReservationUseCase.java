package kr.hhplus.be.server.reservation.usecase;

import kr.hhplus.be.server.reservation.entity.Reservation;
import kr.hhplus.be.server.reservation.entity.ReservationExpirationPolicy;
import kr.hhplus.be.server.reservation.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class MakeReservationUseCase {
    private final ReservationRepository repository;
    private final Clock clock;
    private final ReservationExpirationPolicy expirationPolicy;

    public Reservation makeReservation(UUID userId, UUID seatId) {
        // 활성 예약이 존재하는지 체크
        Optional<Reservation> activeReservation = repository.findBySeatIdAndActive(seatId);
        if (activeReservation.isPresent()) {
            throw new IllegalStateException("Seat Already reserved.");
        }

        // Reservation 생성 (팩토리 메서드)
        Reservation reservation = Reservation.create(userId, seatId, clock, expirationPolicy);

        // 저장
        repository.save(reservation);

        // 반환
        return reservation;
    }
}
