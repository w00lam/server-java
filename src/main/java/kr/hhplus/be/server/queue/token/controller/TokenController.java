package kr.hhplus.be.server.queue.token.controller;

import kr.hhplus.be.server.queue.token.domain.Token;
import kr.hhplus.be.server.queue.token.dto.TokenRequest;
import kr.hhplus.be.server.queue.token.dto.TokenResponse;
import kr.hhplus.be.server.queue.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/queue")
public class TokenController {
    private final TokenService service;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> issueToken(@RequestBody TokenRequest request) {

        if (request.getUser() == null) {
            return ResponseEntity.badRequest().build();
        }

        Token token = service.createToken(request.getUser());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TokenResponse.fromEntity(token));
    }
}
