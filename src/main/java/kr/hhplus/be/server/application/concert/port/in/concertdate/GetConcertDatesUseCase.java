package kr.hhplus.be.server.application.concert.port.in.concertdate;

import java.util.List;

public interface GetConcertDatesUseCase {
    List<GetConcertDatesResult> execute(GetConcertDatesQuery query);
}
