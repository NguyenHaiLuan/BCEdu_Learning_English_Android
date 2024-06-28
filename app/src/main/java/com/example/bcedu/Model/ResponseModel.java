package com.example.bcedu.Model;

public class ResponseModel {
    private boolean success;
    private String message;
    private float total_diem;

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

    public float getTotal_diem() {
        return total_diem;
    }

    public void setTotal_diem(float total_diem) {
        this.total_diem = total_diem;
    }
}
