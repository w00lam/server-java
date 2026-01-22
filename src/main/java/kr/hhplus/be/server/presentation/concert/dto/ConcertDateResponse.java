package kr.hhplus.be.server.presentation.concert.dto;

import kr.hhplus.be.server.application.concert.port.in.concertdate.GetConcertDatesResult;

import java.time.LocalDate;
import java.util.UUID;

public record ConcertDateResponse(UUID id, LocalDate eventDate) {
    public static ConcertDateResponse from(GetConcertDatesResult result) {
        return new ConcertDateResponse(
                result.concertDateId(),
                result.eventDate()
        );
    }
}