package kr.hhplus.be.server.application.concert.port.in;

import java.time.LocalDate;
import java.util.UUID;

public record GetConcertDatesResult(UUID concertDateId, LocalDate eventDate) {
}
