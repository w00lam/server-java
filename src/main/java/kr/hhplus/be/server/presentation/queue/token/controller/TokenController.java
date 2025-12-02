package kr.hhplus.be.server.presentation.queue.token.controller;

import kr.hhplus.be.server.application.queue.token.port.in.IssueTokenCommand;
import kr.hhplus.be.server.application.queue.token.port.in.IssueTokenUseCase;
import kr.hhplus.be.server.presentation.queue.token.dto.IssueTokenRequest;
import kr.hhplus.be.server.presentation.queue.token.dto.IssueTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/queue")
public class TokenController {
    private final IssueTokenUseCase issueTokenUseCase;

    @PostMapping("/token")
    public ResponseEntity<IssueTokenResponse> issueToken(@RequestBody IssueTokenRequest request) {

        if (request.user() == null) {
            return ResponseEntity.badRequest().build();
        }

        var result = issueTokenUseCase.execute(new IssueTokenCommand(request.user().getId()));
        var response = IssueTokenResponse.from(result);

        return ResponseEntity.ok(response);
    }
}
