package kr.hhplus.be.server.common.exception;

public class BusinessRuleViolationException extends IllegalStateException {
    public BusinessRuleViolationException(String message) {
        // Business rule violations represent valid requests blocked by current domain state.
        super(message);
    }
}
