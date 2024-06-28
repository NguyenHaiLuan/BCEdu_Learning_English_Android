package com.example.bcedu.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CauHoi {
    private String cauHoi;
    private List<String> dapAn;
    private String dapAnDung;

    public CauHoi(String cauHoi, List<String> luaChon, String dapAnDung) {
        this.cauHoi = cauHoi;
        this.dapAn = luaChon;
        this.dapAnDung = dapAnDung;
    }

    public String layCauHoi() {
        return cauHoi;
    }

    public List<String> layLuaChon() {
        return dapAn;
    }

    public String layDapAnDung() {
        return dapAnDung;
    }
}

