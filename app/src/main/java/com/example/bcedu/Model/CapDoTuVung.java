package com.example.bcedu.Model;

import java.io.Serializable;

public class CapDoTuVung implements Serializable {
    private int macapdo;
    private String tencapdo;
    private String hinhanhmota;
    private int soluong;

    public CapDoTuVung() {
    }

    public int getMacapdo() {
        return macapdo;
    }

    public void setMacapdo(int macapdo) {
        this.macapdo = macapdo;
    }

    public String getTencapdo() {
        return tencapdo;
    }

    public void setTencapdo(String tencapdo) {
        this.tencapdo = tencapdo;
    }

    public String getHinhanhmota() {
        return hinhanhmota;
    }

    public void setHinhanhmota(String hinhanhmota) {
        this.hinhanhmota = hinhanhmota;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }
}
