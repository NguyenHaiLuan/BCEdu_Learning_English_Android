package com.example.bcedu.Model;

import java.io.Serializable;

public class TuVung implements Serializable {
    private int matuvung;
    private int macapdotuvung;
    private String tentuvung;
    private String loaituvung;
    private String ynghia;
    private String phatam;
    private String viduminhhoa;

    private String hinhanhmota;
    private String audio;

    private int tuvungcuatoi;


    public int getMatuvung() {
        return matuvung;
    }

    public void setMatuvung(int matuvung) {
        this.matuvung = matuvung;
    }

    public int getMacapdotuvung() {
        return macapdotuvung;
    }

    public void setMacapdotuvung(int macapdotuvung) {
        this.macapdotuvung = macapdotuvung;
    }

    public String getTentuvung() {
        return tentuvung;
    }

    public void setTentuvung(String tentuvung) {
        this.tentuvung = tentuvung;
    }

    public String getLoaituvung() {
        return loaituvung;
    }

    public void setLoaituvung(String loaituvung) {
        this.loaituvung = loaituvung;
    }

    public String getYnghia() {
        return ynghia;
    }

    public void setYnghia(String ynghia) {
        this.ynghia = ynghia;
    }

    public String getPhatam() {
        return phatam;
    }

    public void setPhatam(String phatam) {
        this.phatam = phatam;
    }

    public String getViduminhhoa() {
        return viduminhhoa;
    }

    public void setViduminhhoa(String viduminhhoa) {
        this.viduminhhoa = viduminhhoa;
    }

    public String getHinhanhmota() {
        return hinhanhmota;
    }

    public void setHinhanhmota(String hinhanhmota) {
        this.hinhanhmota = hinhanhmota;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public int getTuvungcuatoi() {
        return tuvungcuatoi;
    }

    public void setTuvungcuatoi(int tuvungcuatoi) {
        this.tuvungcuatoi = tuvungcuatoi;
    }
}
