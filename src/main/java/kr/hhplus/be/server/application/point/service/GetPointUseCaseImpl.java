package kr.hhplus.be.server.application.point.service;

import kr.hhplus.be.server.application.point.port.in.GetPointQuery;
import kr.hhplus.be.server.application.point.port.in.GetPointResult;
import kr.hhplus.be.server.application.point.port.in.GetPointUseCase;
import kr.hhplus.be.server.application.point.port.out.PointRepositoryPort;
import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.point.service.PointDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPointUseCaseImpl implements GetPointUseCase {
    private final PointRepositoryPort pointRepositoryPort;
    private final PointDomainService pointDomainService;

    @Override
    public GetPointResult execute(GetPointQuery query) {
        UUID userId = query.userId();
        List<Point> points = pointRepositoryPort.findAllByUserId(userId);
        int balance = pointDomainService.calculateBalance(points);

        return new GetPointResult(userId, balance);
    }
}
