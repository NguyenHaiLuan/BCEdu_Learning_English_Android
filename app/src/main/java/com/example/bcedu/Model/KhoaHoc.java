package com.example.bcedu.Model;

import java.io.Serializable;

public class KhoaHoc implements Serializable {
    private int makhoahoc;
    private int magiangvien;
    private String tenkhoahoc;
    private String ghichukhoahoc;
    private String hinhanhmota;

    public KhoaHoc(int makhoahoc, int magiangvien, String tenkhoahoc, String ghichukhoahoc, String hinhanhmota) {
        this.makhoahoc = makhoahoc;
        this.magiangvien = magiangvien;
        this.tenkhoahoc = tenkhoahoc;
        this.ghichukhoahoc = ghichukhoahoc;
        this.hinhanhmota = hinhanhmota;
    }

    public KhoaHoc() {
    }

    public int getMagiangvien() {
        return magiangvien;
    }

    public void setMagiangvien(int magiangvien) {
        this.magiangvien = magiangvien;
    }

    public int getMakhoahoc() {
        return makhoahoc;
    }

    public void setMakhoahoc(int makhoahoc) {
        this.makhoahoc = makhoahoc;
    }

    public String getTenkhoahoc() {
        return tenkhoahoc;
    }

    public void setTenkhoahoc(String tenkhoahoc) {
        this.tenkhoahoc = tenkhoahoc;
    }

    public String getGhichukhoahoc() {
        return ghichukhoahoc;
    }

    public void setGhichukhoahoc(String ghichukhoahoc) {
        this.ghichukhoahoc = ghichukhoahoc;
    }

    public String getHinhanhmota() {
        return hinhanhmota;
    }

    public void setHinhanhmota(String hinhanhmota) {
        this.hinhanhmota = hinhanhmota;
    }
}
