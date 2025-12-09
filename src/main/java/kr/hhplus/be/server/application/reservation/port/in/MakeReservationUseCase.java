package kr.hhplus.be.server.application.reservation.port.in;

public interface MakeReservationUseCase {
    MakeReservationResult execute(MakeReservationCommand command);
}
