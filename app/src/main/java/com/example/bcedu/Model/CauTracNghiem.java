package com.example.bcedu.Model;

import java.io.Serializable;

public class CauTracNghiem implements Serializable {
    private int macautracnghiem;
    private int mabaikiemtra;
    private String cauhoi;
    private String dapana;
    private String dapanb;
    private String dapanc;
    private String dapand;
    private String dapandung;
    private String audio;

    public int getMacautracnghiem() {
        return macautracnghiem;
    }

    public void setMacautracnghiem(int macautracnghiem) {
        this.macautracnghiem = macautracnghiem;
    }

    public int getMabaikiemtra() {
        return mabaikiemtra;
    }

    public void setMabaikiemtra(int mabaikiemtra) {
        this.mabaikiemtra = mabaikiemtra;
    }

    public String getCauhoi() {
        return cauhoi;
    }

    public void setCauhoi(String cauhoi) {
        this.cauhoi = cauhoi;
    }

    public String getDapana() {
        return dapana;
    }

    public void setDapana(String dapana) {
        this.dapana = dapana;
    }

    public String getDapanb() {
        return dapanb;
    }

    public void setDapanb(String dapanb) {
        this.dapanb = dapanb;
    }

    public String getDapanc() {
        return dapanc;
    }

    public void setDapanc(String dapanc) {
        this.dapanc = dapanc;
    }

    public String getDapand() {
        return dapand;
    }

    public void setDapand(String dapand) {
        this.dapand = dapand;
    }

    public String getDapandung() {
        return dapandung;
    }

    public void setDapandung(String dapandung) {
        this.dapandung = dapandung;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }
}
