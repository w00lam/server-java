package kr.hhplus.be.server.concert.service;

import kr.hhplus.be.server.concert.domain.Concert;
import kr.hhplus.be.server.concert.domain.ConcertDate;
import kr.hhplus.be.server.concert.domain.Seat;
import kr.hhplus.be.server.concert.repository.ConcertRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConcertService 단위 테스트")
public class ConcertServiceTest {
    @Mock
    private ConcertRepository repository;

    @InjectMocks
    private ConcertService service;

    private static final UUID FIXED_CONCERT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FIXED_CONCERT_DATE_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID FIXED_SEAT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final LocalDate FIXED_EVENT_DATE = LocalDate.of(2025, 12, 12);
    private static final LocalDateTime FIXED_NOW = LocalDateTime.of(2025, 12, 12, 0, 0);


    private Concert createConcertFixture() {
        Concert concert = new Concert();
        concert.setId(FIXED_CONCERT_ID);
        return concert;
    }

    private ConcertDate createConcertDateFixture(Concert concert) {
        return new ConcertDate(
                FIXED_CONCERT_DATE_ID,
                concert,
                FIXED_EVENT_DATE,
                FIXED_NOW,
                FIXED_NOW,
                false
        );
    }

    private Seat createSeatFixture(ConcertDate concertDate) {
        return new Seat(
                FIXED_SEAT_ID,
                concertDate,
                "A",
                "1",
                "1",
                "VIP",
                FIXED_NOW,
                FIXED_NOW,
                false
        );
    }

    @Test
    @DisplayName("콘서트 ID로 콘서트 날짜 조회 시 올바른 리스트 반환")
    void getConcertDates_returnsList() {
        Concert concert = createConcertFixture();
        ConcertDate date = createConcertDateFixture(concert);

        when(repository.findDatesByConcertId(concert.getId())).thenReturn(List.of(date));

        List<ConcertDate> result = service.getConcertDates(concert.getId());

        assertEquals(1, result.size());
        assertEquals(concert.getId(), result.get(0).getConcert().getId());
        assertEquals(FIXED_EVENT_DATE, result.get(0).getEventDate());
    }

    @Test
    @DisplayName("콘서트 날짜 ID로 좌석 조회 시 올바른 리스트 반환")
    void getSeatsByConcertDate_returnsList() {
        Concert concert = createConcertFixture();
        ConcertDate concertDate = createConcertDateFixture(concert);
        Seat seat = createSeatFixture(concertDate);

        when(repository.findSeatsByConcertDateId(FIXED_CONCERT_DATE_ID)).thenReturn(List.of(seat));

        List<Seat> result = service.getSeatsByConcertDate(FIXED_CONCERT_DATE_ID);

        assertEquals(1, result.size());
        assertEquals(FIXED_CONCERT_DATE_ID, result.get(0).getConcertDate().getId());
    }
}