package kr.hhplus.be.server.service;

import kr.hhplus.be.server.domain.ConcertDate;
import kr.hhplus.be.server.domain.Seat;
import kr.hhplus.be.server.repository.ConcertDateRepository;
import kr.hhplus.be.server.repository.ReservationRepository;
import kr.hhplus.be.server.repository.SeatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AvailabilityService {
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final ConcertDateRepository concertDateRepository;

    @Transactional(readOnly = true)
    public List<ConcertDate> listConcertDates(UUID concertId) {
        return concertDateRepository.findByConcertIdAndDeletedFalseOrderByEventDateAsc(concertId);
    }

    @Transactional(readOnly = true)
    public List<Seat> listAvailableSeats(UUID concertDateId, String section, String row, String grade) {
        List<UUID> reservedSeatIds = reservationRepository.findActiveSeatIdsByConcertDate(concertDateId);

        return seatRepository.findByConcertDateIdAndDeletedFalse(concertDateId).stream()
                .filter(seat -> !reservedSeatIds.contains(seat.getId()))
                .filter(seat -> section == null || seat.getSection().equals(section))
                .filter(seat -> row == null || seat.getRow().equals(row))
                .filter(seat -> grade == null || seat.getGrade().equals(grade))
                .sorted(Comparator.comparing(Seat::getSection)
                        .thenComparing(Seat::getRow)
                        .thenComparing(Seat::getNumber))
                .collect(Collectors.toList());
    }
}
