package com.instantinsights.api.common.exceptions;

public class UnauthorizedHttpException extends Exception {
    public UnauthorizedHttpException(String message) {
        super(message);
    }

    public UnauthorizedHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
