package kr.hhplus.be.server.application.reservation.port.in;

public interface CancelReservationUseCase {
    CancelReservationResult execute(CancelReservationCommand command);
}
