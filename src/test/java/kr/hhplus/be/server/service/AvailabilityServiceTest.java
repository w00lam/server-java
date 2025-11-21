package kr.hhplus.be.server.service;

import kr.hhplus.be.server.domain.ConcertDate;
import kr.hhplus.be.server.domain.Seat;
import kr.hhplus.be.server.repository.ConcertDateRepository;
import kr.hhplus.be.server.repository.ReservationRepository;
import kr.hhplus.be.server.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvailabilityServiceTest {

    private AvailabilityService availabilityService;
    private SeatRepository seatRepository;
    private ReservationRepository reservationRepository;
    private ConcertDateRepository concertDateRepository;

    @BeforeEach
    void setUp() {
        seatRepository = Mockito.mock(SeatRepository.class);
        reservationRepository = Mockito.mock(ReservationRepository.class);
        concertDateRepository = Mockito.mock(ConcertDateRepository.class);
        availabilityService = new AvailabilityService(seatRepository, reservationRepository, concertDateRepository);
    }

    @Test
    void listConcertDates_returnsSortedActiveDates() {
        UUID concertDateId1 = UUID.fromString("1050d81d-1205-45a7-84dd-619a48a241b7");
        UUID concertDateId2 = UUID.fromString("1050d81d-1205-45a7-84dd-619a48a241b8");
        UUID concertDateId3 = UUID.fromString("1050d81d-1205-45a7-84dd-619a48a241b9");
        UUID concertId = UUID.fromString("1050d81d-1205-45a7-84dd-619a48a241b0");

        LocalDate eventDate = LocalDate.of(2025, 12, 25);
        LocalDateTime createdOrUpdate = LocalDateTime.of(2025, 12, 12, 12, 12, 12);

        ConcertDate d1 = new ConcertDate(concertDateId1, concertId, eventDate, createdOrUpdate, createdOrUpdate, false);
        ConcertDate d2 = new ConcertDate(concertDateId2, concertId, eventDate, createdOrUpdate, createdOrUpdate, false);
        ConcertDate d3 = new ConcertDate(concertDateId3, concertId, eventDate, createdOrUpdate, createdOrUpdate, false);

        when(concertDateRepository.findByConcertIdAndDeletedFalseOrderByEventDateAsc(concertId))
                .thenReturn(Arrays.asList(d1, d2, d3));

        List<ConcertDate> result = availabilityService.listConcertDates(concertId);

        List<LocalDate> sortedDates = result.stream().map(ConcertDate::getEventDate).collect(Collectors.toList());
        assertEquals(Arrays.asList(d1.getEventDate(), d3.getEventDate(), d2.getEventDate()), sortedDates);
    }

    @Test
    void listAvailableSeats_excludesReservedSeatsAndDeleted() {
        UUID seatId1 = UUID.fromString("ae072bfd-6aba-45fc-bbcc-8068bc9fcb86");
        UUID seatId2 = UUID.fromString("ae072bfd-6aba-45fc-bbcc-8068bc9fcb87");
        UUID seatId3 = UUID.fromString("ae072bfd-6aba-45fc-bbcc-8068bc9fcb88");
        UUID concertDateId = UUID.fromString("ae072bfd-6aba-45fc-bbcc-8068bc9fcb89");

        LocalDateTime createdOrUpdate = LocalDateTime.of(2025, 12, 12, 12, 12, 12);


        Seat s1 = new Seat(seatId1, concertDateId, "A", "1", "1", "VIP", createdOrUpdate, createdOrUpdate, false);
        Seat s2 = new Seat(seatId2, concertDateId, "A", "1", "2", "VIP", createdOrUpdate, createdOrUpdate, false);
        Seat s3 = new Seat(seatId3, concertDateId, "A", "1", "3", "VIP", createdOrUpdate, createdOrUpdate, true); // deleted seat

        when(seatRepository.findByConcertDateIdAndDeletedFalse(concertDateId))
                .thenReturn(Arrays.asList(s1, s2));

        when(reservationRepository.findActiveSeatIdsByConcertDate(concertDateId))
                .thenReturn(Collections.singletonList(s2.getId()));

        List<Seat> result = availabilityService.listAvailableSeats(concertDateId, null, null, null);

        assertEquals(1, result.size());
        assertEquals(s1.getId(), result.get(0).getId());
    }

    @Test
    void listAvailableSeats_appliesFilters() {
        UUID seatId1 = UUID.fromString("c9bfadaa-a4e1-46b5-8aef-b4a8b8782cab");
        UUID seatId2 = UUID.fromString("c9bfadaa-a4e1-46b5-8aef-b4a8b8782cac");
        UUID concertDateId = UUID.fromString("c9bfadaa-a4e1-46b5-8aef-b4a8b8782cad");

        LocalDateTime createdOrUpdate = LocalDateTime.of(2025, 12, 12, 12, 12, 12);

        Seat s1 = new Seat(seatId1, concertDateId, "A", "1", "1", "VIP", createdOrUpdate, createdOrUpdate, false);
        Seat s2 = new Seat(seatId2, concertDateId, "B", "1", "2", "R", createdOrUpdate, createdOrUpdate, false);

        when(seatRepository.findByConcertDateIdAndDeletedFalse(concertDateId))
                .thenReturn(Arrays.asList(s1, s2));

        when(reservationRepository.findActiveSeatIdsByConcertDate(concertDateId))
                .thenReturn(Collections.emptyList());

        List<Seat> result = availabilityService.listAvailableSeats(concertDateId, "A", null, null);

        assertEquals(1, result.size());
        assertEquals(s1.getId(), result.get(0).getId());
    }
}
