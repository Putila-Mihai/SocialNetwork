package com.example.socialnetworkapp.domain;

import java.time.LocalDateTime;

public class Message {
    private String message;
    private User from;
    private User to;
    private final LocalDateTime date;

    public Message(User from, User to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        date = LocalDateTime.now();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public User getFrom() {
        return from;
    }

    public User getTo() {
        return to;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
