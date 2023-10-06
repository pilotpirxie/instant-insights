package com.instantinsights.api.common.exceptions;

public class ConflictHttpException extends RuntimeException {
    public ConflictHttpException(String message) {
        super(message);
    }

    public ConflictHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}