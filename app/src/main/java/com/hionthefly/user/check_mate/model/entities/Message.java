package com.hionthefly.user.check_mate.model.entities;

/**
 * Created by User on 20/01/2018.
 */

public class Message {
    private String userId;
    private String message;
    public Message() {
    }

    public Message(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public Message(Message other) {
        this.userId = other.userId;
        this.message = other.message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
