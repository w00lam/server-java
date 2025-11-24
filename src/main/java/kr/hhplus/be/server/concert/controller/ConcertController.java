package kr.hhplus.be.server.concert.controller;

import kr.hhplus.be.server.concert.dto.ConcertDateResponse;
import kr.hhplus.be.server.concert.dto.SeatResponse;
import kr.hhplus.be.server.concert.service.ConcertService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/concerts")
@AllArgsConstructor
public class ConcertController {
    private final ConcertService service;

    @GetMapping("/{concertId}/dates")
    public ResponseEntity<List<ConcertDateResponse>> getConcertDates(@PathVariable UUID concertId) {
        List<ConcertDateResponse> response = service.getConcertDates(concertId)
                .stream()
                .map(ConcertDateResponse::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dates/{concertDateId}/seats")
    public ResponseEntity<List<SeatResponse>> getSeatsByConcertDate(@PathVariable UUID concertDateId) {
        List<SeatResponse> response = service.getSeatsByConcertDate(concertDateId)
                .stream()
                .map(SeatResponse::fromEntity)
                .collect(Collectors.toList());

        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }
}
