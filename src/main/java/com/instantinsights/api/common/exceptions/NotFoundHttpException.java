package com.instantinsights.api.common.exceptions;

public class NotFoundHttpException extends Exception {
    public NotFoundHttpException(String message) {
        super(message);
    }

    public NotFoundHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
