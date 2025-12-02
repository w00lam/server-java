package kr.hhplus.be.server.application.point.port.in;

public interface ChargePointUseCase {
    ChargePointResult execute(ChargePointCommand command);
}
