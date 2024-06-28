package com.example.bcedu.Model;

import java.util.List;

public class XepHangModel {
    boolean success;
    String message;
    List<XepHang> result;

    public XepHangModel(boolean success, String message, List<XepHang> result) {
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

    public List<XepHang> getResult() {
        return result;
    }

    public void setResult(List<XepHang> result) {
        this.result = result;
    }
}
