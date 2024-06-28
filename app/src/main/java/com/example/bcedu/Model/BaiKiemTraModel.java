package com.example.bcedu.Model;

import java.util.List;

public class BaiKiemTraModel {
    boolean success;
    String message;
    List<BaiKiemTra> result;

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

    public List<BaiKiemTra> getResult() {
        return result;
    }

    public void setResult(List<BaiKiemTra> result) {
        this.result = result;
    }
}
