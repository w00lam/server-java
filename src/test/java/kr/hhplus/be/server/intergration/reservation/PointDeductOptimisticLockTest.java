package kr.hhplus.be.server.intergration.reservation;

import kr.hhplus.be.server.application.point.port.in.DeductPointCommand;
import kr.hhplus.be.server.application.point.port.in.DeductPointUseCase;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.persistence.user.adapter.UserRepositoryImpl;
import kr.hhplus.be.server.intergration.ReservationIntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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
        // given
        User user = User.builder()
                .name("test")
                .email("test@test.com")
                .points(1000000)
                .build();

        userRepository.save(user);

        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();

        DeductPointCommand command =
                new DeductPointCommand(user.getId(), 100);

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();      // 준비 완료
                    startLatch.await();          // 동시에 출발

                    deductPointUseCase.execute(command);
                    success.incrementAndGet();

                } catch (ObjectOptimisticLockingFailureException e) {
                    fail.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();       // 모든 스레드 준비될 때까지 대기
        startLatch.countDown();   // 🔥 동시에 시작
        doneLatch.await();

        executor.shutdown();

        // then
        User result = userRepository.findById(user.getId());

        System.out.println("success = " + success.get());
        System.out.println("fail = " + fail.get());

        assertThat(success.get()).isEqualTo(1);
        assertThat(fail.get()).isEqualTo(threadCount - 1);
        assertThat(result.getPoints()).isEqualTo(1000000 - 100);
    }
}
