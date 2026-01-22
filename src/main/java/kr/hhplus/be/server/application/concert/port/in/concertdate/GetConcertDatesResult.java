package kr.hhplus.be.server.application.concert.port.in.concertdate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record GetConcertDatesResult(UUID concertDateId, LocalDate eventDate) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
