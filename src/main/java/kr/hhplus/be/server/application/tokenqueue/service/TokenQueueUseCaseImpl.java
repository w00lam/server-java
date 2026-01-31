package kr.hhplus.be.server.application.tokenqueue.service;

import kr.hhplus.be.server.application.tokenqueue.port.in.TokenQueueUseCase;
import kr.hhplus.be.server.application.tokenqueue.port.out.TokenQueueRepositoryPort;
import kr.hhplus.be.server.domain.tokenqueue.model.TokenQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenQueueUseCaseImpl implements TokenQueueUseCase {
    private final TokenQueueRepositoryPort tokenQueueRepository;

    @Override
    public void enqueueUser(String userId) {
        TokenQueue tokenQueue = new TokenQueue(userId, Instant.now().toEpochMilli());
        tokenQueueRepository.addUser(tokenQueue);
    }

    @Override
    public void dequeueUser() {
        String nextUser = tokenQueueRepository.getNextUser();
        if (nextUser != null) {
            tokenQueueRepository.removeUser(nextUser);
        }
    }

    @Override
    public Integer getUserRank(String userId) {
        return tokenQueueRepository.getUserRank(userId);
    }

    @Override
    public Integer getQueueLength() {
        return tokenQueueRepository.getQueueLength();
    }

    @Override
    public String getNextUser() {
        return tokenQueueRepository.getNextUser();
    }
}
