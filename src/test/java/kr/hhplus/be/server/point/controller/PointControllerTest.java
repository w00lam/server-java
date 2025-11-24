package kr.hhplus.be.server.point.controller;

import kr.hhplus.be.server.point.dto.ChargePointRequest;
import kr.hhplus.be.server.point.dto.ChargePointResponse;
import kr.hhplus.be.server.point.dto.PointBalanceResponse;
import kr.hhplus.be.server.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PointController 단위 테스트")
public class PointControllerTest {
    @Mock
    private PointService service;

    @InjectMocks
    private PointController controller;

    private final UUID FIXED_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    @DisplayName("충전 요청 후 ChargePointResponse 반환")
    void chargePoint_returnsResponse() {
        ChargePointRequest request = new ChargePointRequest();
        request.setUserId(FIXED_USER_ID);
        request.setAmount(1000);

        when(service.getPointBalance(FIXED_USER_ID)).thenReturn(1000);

        ResponseEntity<ChargePointResponse> response = controller.chargePoint(request);

        assertEquals(FIXED_USER_ID, response.getBody().getUserId());
        assertEquals(1000, response.getBody().getAmount());
        assertEquals(1000, response.getBody().getBalance());

        verify(service, times(1)).chargePoint(FIXED_USER_ID, 1000);
    }

    @Test
    @DisplayName("사용자 포인트 조회 시 PointBalanceResponse 반환")
    void getPointBalance_returnsResponse() {
        when(service.getPointBalance(FIXED_USER_ID)).thenReturn(500);

        ResponseEntity<PointBalanceResponse> response = controller.getPointBalance(FIXED_USER_ID);

        assertEquals(FIXED_USER_ID, response.getBody().getUserId());
        assertEquals(500, response.getBody().getBalance());
    }
}
