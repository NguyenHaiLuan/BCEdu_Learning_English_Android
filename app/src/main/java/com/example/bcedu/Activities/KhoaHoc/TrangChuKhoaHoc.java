package com.example.bcedu.Activities.KhoaHoc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bcedu.Activities.KiemTra.LobbyKiemTra;
import com.example.bcedu.Activities.LiveStream.JoinActivity;
import com.example.bcedu.Adapter.BaiKiemTraAdapter;
import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.Model.BaiKiemTra;
import com.example.bcedu.Model.KhoaHoc;
import com.example.bcedu.Model.NguoiDung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Service.MusicService;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TrangChuKhoaHoc extends AppCompatActivity {
    private ShapeableImageView avtKhoaHoc;
    private KhoaHoc khoaHoc = new KhoaHoc();
    private ImageButton back;
    private TextView tenGiangVien, tenKhoaHoc;
    private String tenGV;
    private RelativeLayout kiemtra, lophoc;
    private ApiHocTap apiHocTap;
    private TextView text_kt;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private DialogAppLoading dialogAppLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        anhXa();
        dialogAppLoading.show();
        getDuLieuKhoaHoc();

        if (Utils.nguoiDungCurrent.getManguoidung() == khoaHoc.getMagiangvien()){
            text_kt.setText("Quản lý bài KT");
        }

        clickEvent();
    }

    private void getDuLieuKhoaHoc() {
        int maxacnhan;
        maxacnhan = getIntent().getIntExtra("maXacNhan", 0);

        if (maxacnhan == 69){// mã xác nhận được truyền từ khoá học theo giảng viên
            khoaHoc = (KhoaHoc) getIntent().getSerializableExtra("chitietKhoahoc");
        } else if (maxacnhan == 45){ // mã xác nhận được truyền từ khoá học theo học viên
            khoaHoc = (KhoaHoc) getIntent().getSerializableExtra("chitietKhoahoc45");
        }
        else {
            khoaHoc = (KhoaHoc) getIntent().getSerializableExtra("khoaHocCT");
        }

        String linkAnh;
        linkAnh = khoaHoc.getHinhanhmota();
        Glide.with(getApplicationContext()).load(Utils.BASE_URL + "images/" +linkAnh).into(avtKhoaHoc);

        // lấy thông tin của giảng viên
        compositeDisposable.add(apiHocTap.getGiangVien_API(khoaHoc.getMakhoahoc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model -> {
                            if (model.isSuccess()) {
                                NguoiDung giangVien = model.getResult().get(0);
                                tenGV = giangVien.getTennguoidung();
                                tenGiangVien.setText(tenGV);

                                dialogAppLoading.cancel();
                            }
                            else {
                                dialogAppLoading.cancel();
                                StyleableToast.makeText(this, "Chưa có dữ liệu nào được thêm!", R.style.warning).show();
                            }
                        },
                        throwable -> {
                            dialogAppLoading.cancel();
                            StyleableToast.makeText(this, "Không thể kết nối với server!", R.style.error_toast).show();
                            Log.e("Loi", "get_xephang: " + throwable.getMessage());
                        }
                ));

        tenKhoaHoc.setText(khoaHoc.getTenkhoahoc());
    }

    private void clickEvent() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        kiemtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LobbyKiemTra.class);
                intent.putExtra("khoahoc_tckh", khoaHoc);
                startActivity(intent);
            }
        });

        lophoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });
    }

    private void anhXa() {
        avtKhoaHoc = findViewById(R.id.hinhKhoaHoc_TCKH);
        back = findViewById(R.id.backTCKH);

        tenGiangVien = findViewById(R.id.tenGV_TCKH);
        tenKhoaHoc = findViewById(R.id.tenKhoaHoc_TCKH);

        kiemtra = findViewById(R.id.kiemtra_relative);
        lophoc = findViewById(R.id.lophoconl_relative);
        text_kt = findViewById(R.id.tkt);

        dialogAppLoading = new DialogAppLoading(this);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trang_chu_khoa_hoc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bắt đầu lại dịch vụ phát nhạc nền khi Activity được tiếp tục
        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);
    }
}