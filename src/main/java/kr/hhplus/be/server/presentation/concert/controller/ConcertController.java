package kr.hhplus.be.server.presentation.concert.controller;

import kr.hhplus.be.server.application.concert.port.in.GetConcertDatesQuery;
import kr.hhplus.be.server.application.concert.port.in.GetConcertDatesUseCase;
import kr.hhplus.be.server.application.concert.port.in.GetSeatsQuery;
import kr.hhplus.be.server.application.concert.port.in.GetSeatsUseCase;
import kr.hhplus.be.server.presentation.concert.dto.ConcertDateResponse;
import kr.hhplus.be.server.presentation.concert.dto.SeatResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/concerts")
@AllArgsConstructor
public class ConcertController {
    private final GetConcertDatesUseCase getConcertDatesUseCase;
    private final GetSeatsUseCase getSeatsUseCase;

    @GetMapping("/{concertId}/dates")
    public ResponseEntity<List<ConcertDateResponse>> getConcertDates(@PathVariable UUID concertId) {
        var results = getConcertDatesUseCase.execute(new GetConcertDatesQuery(concertId));
        var response = results.stream().map(ConcertDateResponse::from).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dates/{concertDateId}/seats")
    public ResponseEntity<List<SeatResponse>> getSeatsByConcertDate(@PathVariable UUID concertDateId) {
        var results = getSeatsUseCase.execute(new GetSeatsQuery(concertDateId));
        var response = results.stream().map(SeatResponse::from).toList();

        return ResponseEntity.ok(response);
    }
}
