package kr.hhplus.be.server.common.presentation;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {
    private static final String SUCCESS_MESSAGE = "요청이 성공했습니다.";

    public static <T> ApiResponse<T> ok(T data) {
        // Keep successful API responses consistent across controllers.
        return new ApiResponse<>(HttpStatus.OK.value(), SUCCESS_MESSAGE, data);
    }

    public static ApiResponse<Void> ok() {
        return ok(null);
    }
}
