package kr.hhplus.be.server.queue.token.dto;

import kr.hhplus.be.server.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {
    private User user;
}
