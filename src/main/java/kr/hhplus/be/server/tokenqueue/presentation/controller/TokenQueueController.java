package kr.hhplus.be.server.tokenqueue.presentation.controller;

import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.presentation.ApiResponse;
import kr.hhplus.be.server.tokenqueue.application.port.in.TokenQueueUseCase;
import kr.hhplus.be.server.tokenqueue.presentation.dto.TokenQueueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/queue/token")
public class TokenQueueController {
    private final TokenQueueUseCase tokenQueueUseCase;

    @PostMapping("/enqueue")
    public ResponseEntity<ApiResponse<Void>> enqueue(@RequestBody TokenQueueRequest request) {
        validateTokenQueueRequest(request);
        tokenQueueUseCase.enqueueUser(request.userId());
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @PostMapping("/dequeue")
    public ResponseEntity<ApiResponse<Void>> dequeue() {
        tokenQueueUseCase.dequeueUser();
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @GetMapping("/rank/{userId}")
    public ResponseEntity<ApiResponse<Integer>> getRank(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(tokenQueueUseCase.getUserRank(userId)));
    }

    @GetMapping("/length")
    public ResponseEntity<ApiResponse<Integer>> getLength() {
        return ResponseEntity.ok(ApiResponse.ok(tokenQueueUseCase.getQueueLength()));
    }

    @GetMapping("/next")
    public ResponseEntity<ApiResponse<String>> getNextUser() {
        return ResponseEntity.ok(ApiResponse.ok(tokenQueueUseCase.getNextUser()));
    }

    private void validateTokenQueueRequest(TokenQueueRequest request) {
        // Token queue commands require an explicit user id at the HTTP boundary.
        if (request == null) throw new ClientInputException(ErrorCode.REQUEST_BODY_REQUIRED, "요청 본문은 필수입니다.");
        if (request.userId() == null || request.userId().isBlank()) throw new ClientInputException(ErrorCode.USER_ID_REQUIRED, "사용자 ID는 필수입니다.");
    }
}
