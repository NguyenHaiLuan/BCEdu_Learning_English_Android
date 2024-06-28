package com.example.bcedu.Model;

import java.io.Serializable;

public class KetQuaKiemTra implements Serializable {
    private int maketquakiemtra;
    private int mabaikiemtra;
    private int mahocvien;
    private float diemso;

    public int getMaketquakiemtra() {
        return maketquakiemtra;
    }

    public void setMaketquakiemtra(int maketquakiemtra) {
        this.maketquakiemtra = maketquakiemtra;
    }

    public int getMabaikiemtra() {
        return mabaikiemtra;
    }

    public void setMabaikiemtra(int mabaikiemtra) {
        this.mabaikiemtra = mabaikiemtra;
    }

    public int getMahocvien() {
        return mahocvien;
    }

    public void setMahocvien(int mahocvien) {
        this.mahocvien = mahocvien;
    }

    public float getDiemso() {
        return diemso;
    }

    public void setDiemso(float diemso) {
        this.diemso = diemso;
    }
}
