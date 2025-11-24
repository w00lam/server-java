package kr.hhplus.be.server.point.repository;

import kr.hhplus.be.server.point.domain.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PointRepository extends JpaRepository<PointTransaction, UUID> {
    List<PointTransaction> findByUserId(UUID userId);
}
