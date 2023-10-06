package com.instantinsights.api.common.exceptions;

public class ServiceUnavailableHttpException extends RuntimeException {
    public ServiceUnavailableHttpException(String message) {
        super(message);
    }

    public ServiceUnavailableHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
