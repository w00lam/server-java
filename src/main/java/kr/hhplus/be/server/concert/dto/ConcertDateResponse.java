package kr.hhplus.be.server.concert.dto;

import kr.hhplus.be.server.concert.domain.ConcertDate;

import java.time.LocalDate;
import java.util.UUID;

public record ConcertDateResponse(
        UUID id,
        UUID concertId,
        LocalDate eventDate
) {
    public static ConcertDateResponse fromEntity(ConcertDate entity) {
        return new ConcertDateResponse(
                entity.getId(),
                entity.getConcertId(),
                entity.getEventDate()
        );
    }
}