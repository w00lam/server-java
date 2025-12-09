package kr.hhplus.be.server.application.point.service;

import kr.hhplus.be.server.application.point.port.in.ChargePointCommand;
import kr.hhplus.be.server.application.point.port.in.ChargePointResult;
import kr.hhplus.be.server.application.point.port.in.ChargePointUseCase;
import kr.hhplus.be.server.application.point.port.out.PointRepositoryPort;
import kr.hhplus.be.server.application.user.port.out.UserRepositoryPort;
import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.point.service.PointDomainService;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChargePointUseCaseImpl implements ChargePointUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final PointRepositoryPort pointRepositoryPort;
    private final PointDomainService pointDomainService;

    @Override
    public ChargePointResult execute(ChargePointCommand command) {
        User user = userRepositoryPort.findById(command.userId());
        Point point = pointDomainService.createCharge(user, command.amount());
        Point saved = pointRepositoryPort.save(point);

        return new ChargePointResult(saved.getId(), saved.getAmount());
    }
}
