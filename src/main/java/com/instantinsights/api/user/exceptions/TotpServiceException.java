package com.instantinsights.api.user.exceptions;

public class TotpServiceException extends Exception {
    public TotpServiceException(String message) {
        super(message);
    }

    public TotpServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}