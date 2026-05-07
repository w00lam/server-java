package kr.hhplus.be.server.unit.domain.point.service;

import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.model.PointType;
import kr.hhplus.be.server.point.domain.service.PointDomainService;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PointDomainServiceTest extends BaseUnitTest {
    private final PointDomainService service = new PointDomainService();

    @Test
    @DisplayName("createCharge: 양수 금액은 정상 생성")
    void createCharge_valid() {
        User user = User.builder().id(fixedUUID()).build();
        Point point = service.createCharge(user, 1000);

        assertNotNull(point);
        assertEquals(user, point.getUser());
        assertEquals(1000, point.getAmount());
        assertEquals(PointType.CHARGE, point.getType());
        assertFalse(point.isDeleted());
    }

    @Test
    @DisplayName("createCharge: 0 이하 금액은 예외 발생")
    void createCharge_invalid() {
        User user = User.builder().id(fixedUUID()).build();

        assertThrows(IllegalArgumentException.class, () -> service.createCharge(user, 0));
        assertThrows(IllegalArgumentException.class, () -> service.createCharge(user, -100));
    }
}
