package kr.hhplus.be.server.application.concert.service;

import java.util.UUID;

public record ConcertRankingItem(UUID concertId, long reservationCount) {
}
