package com.example.bcedu.Model;

import java.util.List;

public class CapDoTuVungModel {
    boolean success;
    String message;
    List<CapDoTuVung> result;

    public CapDoTuVungModel(boolean success, String message, List<CapDoTuVung> result) {
        this.success = success;
        this.message = message;
        this.result = result;
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

    public List<CapDoTuVung> getResult() {
        return result;
    }

    public void setResult(List<CapDoTuVung> result) {
        this.result = result;
    }
}
