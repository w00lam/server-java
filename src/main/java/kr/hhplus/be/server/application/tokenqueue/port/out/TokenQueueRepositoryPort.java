package kr.hhplus.be.server.application.tokenqueue.port.out;

import kr.hhplus.be.server.domain.tokenqueue.model.TokenQueue;

public interface TokenQueueRepositoryPort {
    void addUser(TokenQueue tokenQueue);
    void removeUser(String userId);
    Integer getUserRank(String userId);
    Integer getQueueLength();
    String getNextUser();
}
