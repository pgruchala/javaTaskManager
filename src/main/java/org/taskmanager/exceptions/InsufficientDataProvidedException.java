package org.taskmanager.exceptions;

public class InsufficientDataProvidedException extends RuntimeException {
    public InsufficientDataProvidedException(String message) {
        super(message);
    }
    public InsufficientDataProvidedException(String message, Throwable cause) {
        super(message,cause);
    }
}
