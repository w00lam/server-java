package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.dto.ConcertDateDto;
import kr.hhplus.be.server.dto.SeatDto;
import kr.hhplus.be.server.service.AvailabilityService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/concerts")
public class AvailabilityController {
    private final AvailabilityService service;

    @GetMapping("/{concertId}/dates")
    public List<ConcertDateDto> getConcertDates(@PathVariable UUID concertId) {
        return service.listConcertDates(concertId)
                .stream()
                .map(ConcertDateDto::fromEntity)
                .toList();
    }

    @GetMapping("/{concertId}/dates/{concertDateId}/seats")
    public List<SeatDto> getAvailableSeats(@PathVariable UUID concertDateId,
                                           @RequestParam(required = false) String section,
                                           @RequestParam(required = false) String row,
                                           @RequestParam(required = false) String grade) {
        return service.listAvailableSeats(concertDateId, section, row, grade)
                .stream()
                .map(SeatDto::fromEntity)
                .toList();
    }
}
