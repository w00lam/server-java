package kr.hhplus.be.server.application.event.listener;

import kr.hhplus.be.server.application.event.port.out.ReservationEventProducerPort;
import kr.hhplus.be.server.application.reservation.event.ReservationConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationConfirmedEventListener {
    private final ReservationEventProducerPort reservationEventProducerPort;

    @Async
    @EventListener
    public void handle(ReservationConfirmedEvent event) {
        // DB 트랜잭션이 성공적으로 커밋된 후, 카프카로 이벤트를 발행합니다.
        reservationEventProducerPort.sendConfirmedEvent(event);
    }
}
