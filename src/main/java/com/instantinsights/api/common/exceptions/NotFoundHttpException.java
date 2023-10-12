package com.instantinsights.api.common.exceptions;

public class NotFoundHttpException extends NotFoundException {
    public NotFoundHttpException(String message) {
        super(message);
    }

    public NotFoundHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
