package com.instantinsights.api.common.exceptions;

public class ForbiddenHttpException extends Exception {
    public ForbiddenHttpException(String message) {
        super(message);
    }

    public ForbiddenHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
