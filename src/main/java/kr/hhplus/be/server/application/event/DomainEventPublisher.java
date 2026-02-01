package kr.hhplus.be.server.application.event;

public interface  DomainEventPublisher {
    void publish(Object event);
}
