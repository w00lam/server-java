package kr.hhplus.be.server.infrastructure.persistence.point.adapter;

import kr.hhplus.be.server.application.point.port.out.PointRepositoryPort;
import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.infrastructure.persistence.jpa.JpaPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepositoryPort {
    private final JpaPointRepository jpa;

    @Override
    public List<Point> findAllByUserId(UUID userId) {
        return jpa.findAllByUser_Id(userId);
    }

    @Override
    public Point save(Point point) {
        return jpa.save(point);
    }
}
