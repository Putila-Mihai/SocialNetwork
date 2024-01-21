package com.example.socialnetworkapp.domain.exceptions;

public class RejectedRequestException extends RuntimeException {
    public RejectedRequestException() {
    }

    public RejectedRequestException(String message) {
        super(message);
    }

    public RejectedRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RejectedRequestException(Throwable cause) {
        super(cause);
    }

    public RejectedRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
