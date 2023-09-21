package com.instantinsights.api.exceptions;

public class AccountServiceException extends Exception {
    public AccountServiceException(String message) {
        super(message);
    }

    public AccountServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
