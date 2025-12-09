package kr.hhplus.be.server.infrastructure.persistence.jpa;

import kr.hhplus.be.server.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<User, UUID> {
}
