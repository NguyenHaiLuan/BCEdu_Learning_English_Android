package com.example.bcedu.Model;

import java.util.List;

public class KhoaHocModel {
     boolean success;
     String message;
     List<KhoaHoc> result;

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

    public List<KhoaHoc> getResult() {
        return result;
    }

    public void setResult(List<KhoaHoc> result) {
        this.result = result;
    }
}
