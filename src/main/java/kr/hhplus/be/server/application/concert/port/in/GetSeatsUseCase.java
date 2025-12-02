package kr.hhplus.be.server.application.concert.port.in;

import java.util.List;

public interface GetSeatsUseCase {
    List<GetSeatsResult> execute(GetSeatsQuery query);
}
