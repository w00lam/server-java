package kr.hhplus.be.server.common.presentation;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.CodedException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientInputException.class)
    public ProblemDetail handleClientInput(ClientInputException exception) {
        return problem(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ProblemDetail handleBusinessRuleViolation(BusinessRuleViolationException exception) {
        return problem(HttpStatus.CONFLICT, exception);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadableMessage(HttpMessageNotReadableException exception) {
        return problem(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST_BODY, "요청 본문을 읽을 수 없습니다.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return problem(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST_PARAMETER, "요청 파라미터 형식이 올바르지 않습니다.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFound(NoResourceFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, "요청한 리소스를 찾을 수 없습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception exception) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    }

    private ProblemDetail problem(HttpStatus status, RuntimeException exception) {
        // ProblemDetail keeps API error responses consistent without custom DTO plumbing.
        var problem = ProblemDetail.forStatus(status);
        problem.setDetail(exception.getMessage());
        if (exception instanceof CodedException codedException) {
            problem.setProperty("code", codedException.errorCode().name());
        }
        return problem;
    }

    private ProblemDetail problem(HttpStatus status, ErrorCode errorCode, String detail) {
        // Generic framework exceptions still expose stable codes without leaking internals.
        var problem = ProblemDetail.forStatus(status);
        problem.setDetail(detail);
        problem.setProperty("code", errorCode.name());
        return problem;
    }
}
