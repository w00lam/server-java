package kr.hhplus.be.server.application.point.port.in;

public interface GetPointUseCase {
    GetPointResult execute(GetPointQuery query);
}
