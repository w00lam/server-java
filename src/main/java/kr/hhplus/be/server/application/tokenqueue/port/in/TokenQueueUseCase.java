package kr.hhplus.be.server.application.tokenqueue.port.in;

public interface TokenQueueUseCase {
    void enqueueUser(String userId);
    void dequeueUser();
    Integer getUserRank(String userId);
    Integer getQueueLength();
    String getNextUser();
}
