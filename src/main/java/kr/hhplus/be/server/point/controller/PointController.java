package kr.hhplus.be.server.point.controller;

import kr.hhplus.be.server.point.dto.ChargePointRequest;
import kr.hhplus.be.server.point.dto.ChargePointResponse;
import kr.hhplus.be.server.point.dto.PointBalanceResponse;
import kr.hhplus.be.server.point.service.PointService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/points")
public class PointController {
    private final PointService service;

    @PostMapping("/charge")
    public ResponseEntity<ChargePointResponse> chargePoint(@RequestBody ChargePointRequest request) {
        validateChargeRequest(request);
        service.chargePoint(request.getUserId(), request.getAmount());
        int balance = service.getPointBalance(request.getUserId());
        ChargePointResponse response = new ChargePointResponse(request.getUserId(), request.getAmount(), balance);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<PointBalanceResponse> getPointBalance(@PathVariable UUID userId) {
        return ResponseEntity.ok(new PointBalanceResponse(userId, service.getPointBalance(userId)));
    }

    private void validateChargeRequest(ChargePointRequest request) {
        if (request.getAmount() <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (request.getUserId() == null) throw new IllegalArgumentException("UserId is required");
    }
}
