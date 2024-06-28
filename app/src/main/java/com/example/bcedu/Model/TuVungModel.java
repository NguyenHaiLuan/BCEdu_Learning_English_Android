package com.example.bcedu.Model;

import java.util.List;

public class TuVungModel {
    boolean success;
    String message;
    List<TuVung> result;

    public TuVungModel(boolean success, String message, List<TuVung> result) {
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

    public List<TuVung> getResult() {
        return result;
    }

    public void setResult(List<TuVung> result) {
        this.result = result;
    }
}
