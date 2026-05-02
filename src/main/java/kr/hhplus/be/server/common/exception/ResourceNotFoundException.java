package kr.hhplus.be.server.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Object resourceId) {
        // Keep lookup failures explicit so HTTP adapters can map them to 404.
        super(resourceName + " not found: " + resourceId);
    }
}
