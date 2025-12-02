package kr.hhplus.be.server.infrastructure.point.persistence.jpa;

import kr.hhplus.be.server.domain.point.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaPointRepository extends JpaRepository<Point, UUID> {
    List<Point> findAllByUser_Id(UUID userId);
}
