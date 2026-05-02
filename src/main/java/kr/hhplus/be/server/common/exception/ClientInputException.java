package kr.hhplus.be.server.common.exception;

public class ClientInputException extends IllegalArgumentException {
    public ClientInputException(String message) {
        // Client input errors keep bad requests separate from domain state conflicts.
        super(message);
    }
}
