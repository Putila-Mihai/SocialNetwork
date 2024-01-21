package com.example.socialnetworkapp.domain.exceptions;

public class AlreadyFriendsException extends RuntimeException {
    public AlreadyFriendsException() {
    }

    public AlreadyFriendsException(String message) {
        super(message);
    }

    public AlreadyFriendsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyFriendsException(Throwable cause) {
        super(cause);
    }

    public AlreadyFriendsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
