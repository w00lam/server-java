package kr.hhplus.be.server.concert.controller;

import kr.hhplus.be.server.concert.dto.ConcertDateResponse;
import kr.hhplus.be.server.concert.dto.SeatResponse;
import kr.hhplus.be.server.concert.domain.ConcertDate;
import kr.hhplus.be.server.concert.domain.Seat;
import kr.hhplus.be.server.concert.service.ConcertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConcertController 단위 테스트")
public class ConcertControllerTest {
    @Mock
    ConcertService service;

    @InjectMocks
    ConcertController controller;

    private static final UUID FIXED_CONCERT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FIXED_CONCERT_DATE_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID FIXED_SEAT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private static final LocalDate FIXED_EVENT_DATE = LocalDate.of(2025, 12, 12);
    private static final LocalDateTime FIXED_NOW = LocalDateTime.of(2025, 12, 12, 0, 0);

    private ConcertDate createConcertDateFixture() {
        return new ConcertDate(
                FIXED_CONCERT_DATE_ID,
                FIXED_CONCERT_ID,
                FIXED_EVENT_DATE,
                FIXED_NOW,
                FIXED_NOW,
                false
        );
    }

    private Seat createSeatFixture() {
        return new Seat(
                FIXED_SEAT_ID,
                FIXED_CONCERT_DATE_ID,
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
    @DisplayName("콘서트 ID로 콘서트 날짜 목록을 조회하면 올바른 응답을 반환한다")
    void getConcertDates_returnsList() {
        ConcertDate date = createConcertDateFixture();
        when(service.getConcertDates(FIXED_CONCERT_ID)).thenReturn(List.of(date));

        ResponseEntity<List<ConcertDateResponse>> response = controller.getConcertDates(FIXED_CONCERT_ID);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());

        ConcertDateResponse dto = response.getBody().get(0);

        assertEquals(FIXED_CONCERT_ID, dto.concertId());
        assertEquals(FIXED_EVENT_DATE, dto.eventDate());
    }

    @Test
    @DisplayName("콘서트 날짜 ID로 좌석 목록을 조회하면 올바른 응답을 반환한다")
    void getSeatsByConcertDate_returnsList() {
        Seat seat = createSeatFixture();
        when(service.getSeatsByConcertDate(FIXED_CONCERT_DATE_ID)).thenReturn(List.of(seat));

        ResponseEntity<List<SeatResponse>> response = controller.getSeatsByConcertDate(FIXED_CONCERT_DATE_ID);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());

        SeatResponse dto = response.getBody().get(0);

        assertEquals(FIXED_CONCERT_DATE_ID, dto.concertDateId());
        assertEquals("A", dto.section());
        assertEquals("1", dto.row());
        assertEquals("1", dto.number());
        assertEquals("VIP", dto.grade());
    }

    @Test
    @DisplayName("존재하지 않는 콘서트 ID 조회 시 404 반환")
    void getConcertDates_notFound() {
        when(service.getConcertDates(FIXED_CONCERT_ID)).thenReturn(List.of());

        ResponseEntity<List<ConcertDateResponse>> response =
                controller.getConcertDates(FIXED_CONCERT_ID);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("존재하지 않는 콘서트 날짜 ID 좌석 조회 시 404 반환")
    void getSeatsByConcertDate_notFound() {
        when(service.getSeatsByConcertDate(FIXED_CONCERT_DATE_ID)).thenReturn(List.of());

        ResponseEntity<List<SeatResponse>> response =
                controller.getSeatsByConcertDate(FIXED_CONCERT_DATE_ID);

        assertEquals(404, response.getStatusCode().value());
    }
}