package kr.hhplus.be.server.point.service;

import kr.hhplus.be.server.point.entity.PointTransaction;
import kr.hhplus.be.server.point.entity.PointType;
import kr.hhplus.be.server.point.repository.PointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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

    private final UUID FIXED_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    @DisplayName("충전 금액이 음수면 예외 발생")
    void chargePoint_negativeAmount_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.chargePoint(FIXED_USER_ID, -100));
    }

    @Test
    @DisplayName("사용자 포인트 조회 시 합산 반환")
    void getPointBalance_returnsCorrectSum() {
        when(repository.findByUserId(FIXED_USER_ID)).thenReturn(List.of(
                new PointTransaction(FIXED_USER_ID, 1000, PointType.CHARGE),
                new PointTransaction(FIXED_USER_ID, -200, PointType.CHARGE)
        ));

        int balance = service.getPointBalance(FIXED_USER_ID);

        assertEquals(800, balance);
    }
}
