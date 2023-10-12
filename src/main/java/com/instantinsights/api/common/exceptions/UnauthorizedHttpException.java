package com.instantinsights.api.common.exceptions;

public class UnauthorizedHttpException extends RuntimeException {
    public UnauthorizedHttpException(String message) {
        super(message);
    }

    public UnauthorizedHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
