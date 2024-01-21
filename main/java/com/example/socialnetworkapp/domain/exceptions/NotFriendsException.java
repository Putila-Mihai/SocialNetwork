package com.example.socialnetworkapp.domain.exceptions;

public class NotFriendsException extends RuntimeException {
    public NotFriendsException() {
    }

    public NotFriendsException(String message) {
        super(message);
    }

    public NotFriendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFriendsException(Throwable cause) {
        super(cause);
    }

    public NotFriendsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
