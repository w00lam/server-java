package kr.hhplus.be.server.infrastructure.event.adapter;

import kr.hhplus.be.server.application.event.port.out.DataPlatformClientPort;
import kr.hhplus.be.server.application.reservation.event.ReservationConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaReservationConsumer {
    private final DataPlatformClientPort dataPlatformClientPort;

    @KafkaListener(topics = "reservation-confirmed", groupId = "data-platform-group")
    public void consumeReservation(ReservationConfirmedEvent event) {
        log.info("카프카로부터 예약 확정 이벤트 수신: {}", event.reservationId());

        // 기존에 외부 API를 호출하던 로직을 여기서 실행
        dataPlatformClientPort.sendReservationConfirmed(event.reservationId(), event.reservationId());
    }
}
