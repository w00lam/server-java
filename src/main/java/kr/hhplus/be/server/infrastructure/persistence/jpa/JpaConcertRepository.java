package kr.hhplus.be.server.infrastructure.persistence.jpa;

import kr.hhplus.be.server.domain.concert.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaConcertRepository extends JpaRepository<Concert, UUID> {
}
