package com.example.bcedu.Model;

import java.io.Serializable;

public class KetQuaOnTap implements Serializable {
    private int maketquaontap;
    private int macapdo;
    private int manguoidung;
    private float diemcaonhat;
    private int status;

    public int getStatus() {
        return status;
    }

    public int getManguoidung() {
        return manguoidung;
    }

    public void setManguoidung(int manguoidung) {
        this.manguoidung = manguoidung;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMaketquaontap() {
        return maketquaontap;
    }

    public void setMaketquaontap(int maketquaontap) {
        this.maketquaontap = maketquaontap;
    }

    public int getMacapdo() {
        return macapdo;
    }

    public void setMacapdo(int macapdo) {
        this.macapdo = macapdo;
    }

    public float getDiemcaonhat() {
        return diemcaonhat;
    }

    public void setDiemcaonhat(float diemcaonhat) {
        this.diemcaonhat = diemcaonhat;
    }
}
