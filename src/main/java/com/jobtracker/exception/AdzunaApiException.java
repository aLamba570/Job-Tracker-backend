package com.jobtracker.exception;

public class AdzunaApiException extends RuntimeException {
    public AdzunaApiException(String message) {
        super(message);
    }

    public AdzunaApiException(String message, Throwable cause) {
        super(message, cause);
    }
}