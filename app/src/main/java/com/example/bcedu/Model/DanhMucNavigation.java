package com.example.bcedu.Model;

public class DanhMucNavigation {
    private String tenDanhMuc;
    private String hinhAnhDanhMuc;

    public DanhMucNavigation(String tenDanhMuc, String hinhAnhDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
        this.hinhAnhDanhMuc = hinhAnhDanhMuc;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public void setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
    }

    public String getHinhAnhDanhMuc() {
        return hinhAnhDanhMuc;
    }

    public void setHinhAnhDanhMuc(String hinhAnhDanhMuc) {
        this.hinhAnhDanhMuc = hinhAnhDanhMuc;
    }
}
