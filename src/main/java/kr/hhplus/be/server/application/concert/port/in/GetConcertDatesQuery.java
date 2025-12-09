package kr.hhplus.be.server.application.concert.port.in;

import java.util.UUID;

public record GetConcertDatesQuery(UUID concertId) {
}