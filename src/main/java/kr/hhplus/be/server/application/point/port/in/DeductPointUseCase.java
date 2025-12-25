package kr.hhplus.be.server.application.point.port.in;

public interface DeductPointUseCase {
    DeductPointResult execute(DeductPointCommand command);
}
