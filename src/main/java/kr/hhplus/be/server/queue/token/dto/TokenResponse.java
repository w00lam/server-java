package kr.hhplus.be.server.queue.token.dto;

import kr.hhplus.be.server.queue.token.domain.Token;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private int position;

    public static TokenResponse fromEntity(Token token) {
        return new TokenResponse(token.getToken(), token.getPosition());
    }
}