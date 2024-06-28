package com.example.bcedu.Model;

import java.util.List;

public class KetQuaKiemTraModel {
    boolean success;
    String message;
    List<KetQuaKiemTra> result;

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

    public List<KetQuaKiemTra> getResult() {
        return result;
    }

    public void setResult(List<KetQuaKiemTra> result) {
        this.result = result;
    }
}
