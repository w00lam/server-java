package kr.hhplus.be.server.application.concert.port.in.concertdate;

import java.util.UUID;

public record GetConcertDatesQuery(UUID concertId) {
}