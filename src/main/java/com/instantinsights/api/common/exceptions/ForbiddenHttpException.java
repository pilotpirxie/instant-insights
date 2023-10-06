package com.instantinsights.api.common.exceptions;

public class ForbiddenHttpException extends RuntimeException {
    public ForbiddenHttpException(String message) {
        super(message);
    }

    public ForbiddenHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
