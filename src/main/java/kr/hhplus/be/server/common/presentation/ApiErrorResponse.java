package kr.hhplus.be.server.common.presentation;

import kr.hhplus.be.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public record ApiErrorResponse(
        int status,
        String message,
        String code,
        Void data
) {
    public static ApiErrorResponse of(HttpStatus status, ErrorCode errorCode, String message) {
        // Error responses mirror successful responses while keeping a machine-readable code.
        return new ApiErrorResponse(status.value(), message, errorCode.name(), null);
    }
}
