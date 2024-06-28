package com.example.bcedu.Model;

import java.util.List;

public class KetQuaOnTapModel {
    boolean success;
    String message;
    List<KetQuaOnTap> result;

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

    public List<KetQuaOnTap> getResult() {
        return result;
    }

    public void setResult(List<KetQuaOnTap> result) {
        this.result = result;
    }
}
