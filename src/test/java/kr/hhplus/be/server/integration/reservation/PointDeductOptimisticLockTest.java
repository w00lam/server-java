package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.integration.support.ConcurrencyTestSupport;
import kr.hhplus.be.server.point.application.port.in.DeductPointCommand;
import kr.hhplus.be.server.point.application.port.in.DeductPointUseCase;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.infrastructure.persistence.UserRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PointDeductOptimisticLockTest extends ReservationIntegrationTestBase {
    @Autowired
    DeductPointUseCase deductPointUseCase;

    @Autowired
    UserRepositoryImpl userRepository;

    @Test
    @DisplayName("동일 사용자 포인트를 동시에 차감하면 1건만 성공하고 나머지는 실패한다 (낙관적 락)")
    void deduct_point_concurrently_with_optimistic_lock() throws Exception {
        User user = User.builder()
                .name("test")
                .email("test@test.com")
                .points(1000000)
                .build();

        userRepository.save(user);

        int threadCount = 3;
        DeductPointCommand command =
                new DeductPointCommand(user.getId(), 100);

        var result = ConcurrencyTestSupport.runConcurrently(threadCount, index -> {
            try {
                deductPointUseCase.execute(command);
                return true;
            } catch (ObjectOptimisticLockingFailureException expectedRaceLoss) {
                return false;
            }
        });

        User savedUser = userRepository.findById(user.getId());

        assertThat(result.failures()).isEmpty();
        assertThat(result.successes()).containsExactlyInAnyOrder(true, false, false);
        assertThat(savedUser.getPoints()).isEqualTo(1000000 - 100);
    }
}
