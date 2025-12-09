package kr.hhplus.be.server.application.user.port.out;

import kr.hhplus.be.server.domain.user.model.User;

import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);
    User findById(UUID userId);
}
