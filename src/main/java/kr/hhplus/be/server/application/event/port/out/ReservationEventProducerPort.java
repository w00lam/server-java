package kr.hhplus.be.server.application.event.port.out;

import kr.hhplus.be.server.application.reservation.event.ReservationConfirmedEvent;

public interface ReservationEventProducerPort {
    void sendConfirmedEvent(ReservationConfirmedEvent event);
}
