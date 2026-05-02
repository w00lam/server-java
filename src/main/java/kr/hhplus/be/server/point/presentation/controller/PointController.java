package kr.hhplus.be.server.point.presentation.controller;

import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.point.application.port.in.ChargePointCommand;
import kr.hhplus.be.server.point.application.port.in.ChargePointUseCase;
import kr.hhplus.be.server.point.application.port.in.GetPointQuery;
import kr.hhplus.be.server.point.application.port.in.GetPointUseCase;
import kr.hhplus.be.server.point.presentation.dto.ChargePointRequest;
import kr.hhplus.be.server.point.presentation.dto.ChargePointResponse;
import kr.hhplus.be.server.point.presentation.dto.GetPointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class PointController {
    private final ChargePointUseCase chargePointUseCase;
    private final GetPointUseCase getPointUseCase;

    @PostMapping("/charge")
    public ResponseEntity<ChargePointResponse> chargePoint(@RequestBody ChargePointRequest request) {
        validateChargeRequest(request);
        var result = chargePointUseCase.execute(new ChargePointCommand(request.user().getId(), request.amount()));
        var response = ChargePointResponse.from(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<GetPointResponse> getPoint(@PathVariable UUID userId) {
        var result = getPointUseCase.execute(new GetPointQuery(userId));
        var response = GetPointResponse.from(result);

        return ResponseEntity.ok(response);
    }

    private void validateChargeRequest(ChargePointRequest request) {
        // Keep transport validation here so application use cases receive a complete command.
        if (request == null) throw new ClientInputException("Request is required");
        if (request.user() == null) throw new ClientInputException("UserId is required");
        if (request.amount() <= 0) throw new ClientInputException("Amount must be positive");
    }
}
