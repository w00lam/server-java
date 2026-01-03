package kr.hhplus.be.server.application.reservation.port.in;

public interface ConfirmReservationUseCase {
    ConfirmReservationResult execute(ConfirmReservationCommand command);
}
