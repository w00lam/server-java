package kr.hhplus.be.server.point.service;

import kr.hhplus.be.server.point.domain.PointTransaction;
import kr.hhplus.be.server.point.domain.PointType;
import kr.hhplus.be.server.point.repository.PointRepository;
import kr.hhplus.be.server.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PointService 단위 테스트")
public class PointServiceTest {
    @Mock
    private PointRepository repository;

    @InjectMocks
    private PointService service;

    private static final UUID FIXED_RESERVATION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private User createUserFixture() {
        User user = new User();
        user.setId(FIXED_RESERVATION_ID);
        return user;
    }

    @Test
    @DisplayName("충전 금액이 음수면 예외 발생")
    void chargePoint_negativeAmount_throwsException() {
        User user = createUserFixture();
        assertThrows(IllegalArgumentException.class, () -> service.chargePoint(user.getId(), -100));
    }

    @Test
    @DisplayName("사용자 포인트 조회 시 합산 반환")
    void getPointBalance_returnsCorrectSum() {
        User user = createUserFixture();
        when(repository.findByUserId(user.getId())).thenReturn(List.of(
                new PointTransaction(user, 1000, PointType.CHARGE),
                new PointTransaction(user, -200, PointType.CHARGE)
        ));

        int balance = service.getPointBalance(user.getId());

        assertEquals(800, balance);
    }
}
