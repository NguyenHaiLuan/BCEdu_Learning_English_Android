package com.example.bcedu.Model;

import java.io.Serializable;

public class XepHang implements Serializable {
    private int maxephang;
    private String tenxephang;
    private String hinhanh;

    public int getMaxephang() {
        return maxephang;
    }

    public void setMaxephang(int maxephang) {
        this.maxephang = maxephang;
    }

    public String getTenxephang() {
        return tenxephang;
    }

    public void setTenxephang(String tenxephang) {
        this.tenxephang = tenxephang;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }
}
