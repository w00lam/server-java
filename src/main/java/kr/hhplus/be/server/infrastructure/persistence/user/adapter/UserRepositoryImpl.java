package kr.hhplus.be.server.infrastructure.persistence.user.adapter;

import kr.hhplus.be.server.application.user.port.out.UserRepositoryPort;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.persistence.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryPort {
    private final JpaUserRepository jpa;

    @Override
    public User save(User user) {
        return jpa.save(user);
    }

    @Override
    public User findById(UUID userId) {
        return jpa.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
