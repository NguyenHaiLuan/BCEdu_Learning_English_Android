package com.example.bcedu.Model;

import java.util.List;

public class CauTracNghiemModel {
    boolean success;
    String message;
    List<CauTracNghiem> result;

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

    public List<CauTracNghiem> getResult() {
        return result;
    }

    public void setResult(List<CauTracNghiem> result) {
        this.result = result;
    }
}
