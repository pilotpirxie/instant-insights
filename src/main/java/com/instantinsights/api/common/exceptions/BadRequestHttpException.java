package com.instantinsights.api.common.exceptions;

public class BadRequestHttpException extends Exception {
    public BadRequestHttpException(String message) {
        super(message);
    }

    public BadRequestHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
