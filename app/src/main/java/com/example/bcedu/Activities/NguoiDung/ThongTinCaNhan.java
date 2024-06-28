package com.example.bcedu.Activities.NguoiDung;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThongTinCaNhan extends AppCompatActivity {
    private TextView ten, email, sdt, vaitro, gioiTinh, rank, ngaysinh;
    private ImageView hinhRank;
    private ShapeableImageView avt;
    private ImageButton back;
    private Button edit;

    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private DialogAppLoading dialogAppLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        anhXa();
        getDuLieuNguoiDung();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThongTinCaNhan.this, ChinhSuaThongTinCaNhan.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getDuLieuNguoiDung() {
        ten.setText(Utils.nguoiDungCurrent.getTennguoidung().trim());
        email.setText(Utils.nguoiDungCurrent.getEmail().trim());
        sdt.setText(Utils.nguoiDungCurrent.getSodienthoai().trim());
        vaitro.setText(Utils.nguoiDungCurrent.getVaitro().trim());
        gioiTinh.setText(Utils.nguoiDungCurrent.getGioitinh().trim());
        ngaysinh.setText(Utils.nguoiDungCurrent.getNgaysinh());

        rank.setText(Utils.xepHangCuaToi.getTenxephang());

        Glide.with(this)
                .load(Utils.BASE_URL + "images/" + Utils.nguoiDungCurrent.getAnhdaidien())
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(avt);

        Glide.with(ThongTinCaNhan.this).load(Utils.xepHangCuaToi.getHinhanh()).into(hinhRank);
    }

    private void anhXa() {
        ten = findViewById(R.id.tenNguoiDungTTCN);
        email = findViewById(R.id.emailTTCT);
        sdt = findViewById(R.id.soDienThoaiTTCT);
        vaitro = findViewById(R.id.vaiTroTTCT);
        gioiTinh = findViewById(R.id.gioiTinhTTCT);
        rank = findViewById(R.id.rankNguoiDung);

        avt = findViewById(R.id.avatarNguoiDung);
        hinhRank = findViewById(R.id.rankTTCT);

        back = findViewById(R.id.backThongTinChiTiet);
        edit = findViewById(R.id.chinhSuaThongTinBtn);
        ngaysinh = findViewById(R.id.ngaySinhTTCT);
        dialogAppLoading = new DialogAppLoading(this);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thong_tin_ca_nhan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadDuLieu();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void reloadDuLieu(){
        compositeDisposable.add(apiHocTap.dangNhapAPI(Utils.nguoiDungCurrent.getEmail(), Utils.nguoiDungCurrent.getMatkhau())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dangNhapHocVienModel -> {
                            if (dangNhapHocVienModel.isSuccess()){
                                Utils.nguoiDungCurrent = dangNhapHocVienModel.getResult().get(0);
                                getBacRank();
                                getBacRankTiepTheo();
                            }
                            else {
                                StyleableToast.makeText(getApplicationContext(), dangNhapHocVienModel.getMessage(), R.style.error_toast).show();
                                Log.d("log_CTTV1", dangNhapHocVienModel.getMessage());
                            }
                        }, throwable ->{
                            Log.d("log_CTTV", throwable.getMessage());
                        }
                ));
    }

    private void getBacRank() {
        compositeDisposable.add(apiHocTap.getXepHangAPI(Utils.nguoiDungCurrent.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model2 -> {
                            if (model2.isSuccess()) {
                                Utils.xepHangCuaToi = model2.getResult().get(0);
                            } else {
                            }
                        }, throwable -> {
                            Log.e("Loi", "get_xephang: " + throwable.getMessage());
                        }
                ));
    }
    private void getBacRankTiepTheo() {
        compositeDisposable.add(apiHocTap.getXepHangMoiAPI(Utils.nguoiDungCurrent.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model3 -> {
                            if (model3.isSuccess()) {
                                Utils.xepHangTiepTheo = model3.getResult().get(0);
                            } else {
                            }
                        }, throwable -> {
                            Log.e("Loi", "get_xephang2: " + throwable.getMessage());
                        }
                ));
    }
}