package com.example.bcedu.Model;

public class MessageModel {
    private boolean success;
    private String message;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageModel(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}