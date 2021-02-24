package form.library.services;

import form.library.interfaces.ValidationError;

/**
 * Error class, which contains
 * message, path and failed value.
 */
public class ServiceError implements ValidationError {
    private final String message, path;
    private final Object failedValue;

    public ServiceError(String path, String message, Object failedValue) {
        this.path = path;
        this.message = message;
        this.failedValue = failedValue;
    }

    @Override
    public String getMessage() {
        return message;
    }
    @Override
    public String getPath() {
        return path;
    }
    @Override
    public Object getFailedValue() {
        return failedValue;
    }
}
