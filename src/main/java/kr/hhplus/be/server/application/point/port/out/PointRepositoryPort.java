package kr.hhplus.be.server.application.point.port.out;

import kr.hhplus.be.server.domain.point.model.Point;

import java.util.List;
import java.util.UUID;

public interface PointRepositoryPort {
    List<Point> findAllByUserId(UUID userId);
    Point save(Point point);
}
