package com.example.bcedu.Model;

import java.io.Serializable;

public class BaiKiemTra implements Serializable {
    private int mabaikiemtra;
    private int makhoahoc;
    private String tenbaikiemtra;
    private int socauhoi;
    private String hinhanhminhhoa;
    private int thoiluong;

    public int getMabaikiemtra() {
        return mabaikiemtra;
    }

    public void setMabaikiemtra(int mabaikiemtra) {
        this.mabaikiemtra = mabaikiemtra;
    }

    public int getMakhoahoc() {
        return makhoahoc;
    }

    public void setMakhoahoc(int makhoahoc) {
        this.makhoahoc = makhoahoc;
    }

    public String getTenbaikiemtra() {
        return tenbaikiemtra;
    }

    public void setTenbaikiemtra(String tenbaikiemtra) {
        this.tenbaikiemtra = tenbaikiemtra;
    }

    public int getSocauhoi() {
        return socauhoi;
    }

    public void setSocauhoi(int socauhoi) {
        this.socauhoi = socauhoi;
    }

    public String getHinhanhminhhoa() {
        return hinhanhminhhoa;
    }
    public void setHinhanhminhhoa(String hinhanhminhhoa) {
        this.hinhanhminhhoa = hinhanhminhhoa;
    }
    public int getThoiluong() {
        return thoiluong;
    }

    public void setThoiluong(int thoiluong) {
        this.thoiluong = thoiluong;
    }
}
