package kr.hhplus.be.server.concert.dto;

import kr.hhplus.be.server.concert.entity.Seat;

import java.util.UUID;

public record SeatResponse(
        UUID id,
        UUID concertDateId,
        String section,
        String row,
        String number,
        String grade
) {
    public static SeatResponse fromEntity(Seat entity) {
        return new SeatResponse(
                entity.getId(),
                entity.getConcertDateId(),
                entity.getSection(),
                entity.getRow(),
                entity.getNumber(),
                entity.getGrade()
        );
    }
}