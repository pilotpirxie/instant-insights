package com.instantinsights.api.user.exceptions;

public class AccountServiceException extends Exception {
    public AccountServiceException(String message) {
        super(message);
    }

    public AccountServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
