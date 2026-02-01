package kr.hhplus.be.server.application.event.listener;

import kr.hhplus.be.server.application.event.port.out.DataPlatformClientPort;
import kr.hhplus.be.server.application.reservation.event.ReservationConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationConfirmedEventListener {
    private final DataPlatformClientPort dataPlatformClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationConfirmedEvent event) {
        dataPlatformClient.sendReservationConfirmed(event.reservationId(), event.concertId());
    }
}
