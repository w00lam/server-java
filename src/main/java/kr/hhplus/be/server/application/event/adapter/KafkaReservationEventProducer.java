package kr.hhplus.be.server.application.event.adapter;

import kr.hhplus.be.server.application.event.port.out.ReservationEventProducerPort;
import kr.hhplus.be.server.application.reservation.event.ReservationConfirmedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaReservationEventProducer implements ReservationEventProducerPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "reservation-confirmed";

    @Override
    public void sendConfirmedEvent(ReservationConfirmedEvent event){
        // 예약 ID를 키로 사용하여 파티션 순서 보장
        kafkaTemplate.send(TOPIC, String.valueOf(event.reservationId()), event);
    }
}
