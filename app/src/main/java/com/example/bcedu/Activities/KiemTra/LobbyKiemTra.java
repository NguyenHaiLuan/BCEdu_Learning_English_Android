package com.example.bcedu.Activities.KiemTra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bcedu.Adapter.BaiKiemTraAdapter;
import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.Model.BaiKiemTra;
import com.example.bcedu.Model.KhoaHoc;
import com.example.bcedu.Model.NguoiDung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LobbyKiemTra extends AppCompatActivity {

    private ShapeableImageView avtKhoaHoc;
    private KhoaHoc khoaHoc = new KhoaHoc();
    private ImageButton back;
    private TextView tenGiangVien, tenKhoaHoc;
    private String tenGV;
    private CardView khoiNutLenh;
    private ImageButton themBaiKT;
    private ApiHocTap apiHocTap;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private RecyclerView recyclerViewBaiKT;
    private ImageView noDataImage;
    private List<BaiKiemTra> mangBaiKiemTra;
    private BaiKiemTraAdapter adapter;
    private DialogAppLoading dialogAppLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        anhXa();
        dialogAppLoading.show();
        getDuLieuKhoaHoc();

        getDuLieuBaiKT_RecyclerView();

        clickEvent();
    }

    private void getDuLieuBaiKT_RecyclerView() {
        compositeDisposable.add(apiHocTap.getBaiKiemTra_theoMaKhoaHoc(khoaHoc.getMakhoahoc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        baiKiemTraModel -> {
                            if (baiKiemTraModel.isSuccess()) {
                                mangBaiKiemTra = baiKiemTraModel.getResult();
                                adapter = new BaiKiemTraAdapter(mangBaiKiemTra, this);
                                recyclerViewBaiKT.setAdapter(adapter);
                                dialogAppLoading.cancel();
                                if (mangBaiKiemTra.size() > 0) {
                                    noDataImage.setVisibility(View.INVISIBLE);
                                }
                            }
                            else {
                                dialogAppLoading.cancel();
                                StyleableToast.makeText(this, "Đã có lỗi xảy ra! Thử khởi động lại sau vài phút!", R.style.error_toast).show();
                            }
                        }, throwable -> {
                            dialogAppLoading.cancel();
                            StyleableToast.makeText(this, "Không kết nối được với server!", R.style.error_toast).show();
                            Log.e("logTVTCD", "AAA: " + throwable.getMessage());
                        }
                ));

    }

    private void getDuLieuKhoaHoc() {
        khoaHoc = (KhoaHoc) getIntent().getSerializableExtra("khoahoc_tckh");

        String linkAnh;
        linkAnh = khoaHoc.getHinhanhmota();
        Glide.with(getApplicationContext()).load(Utils.BASE_URL + "images/" + linkAnh).into(avtKhoaHoc);

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

                                if (Utils.nguoiDungCurrent.getManguoidung() != giangVien.getManguoidung()) {
                                    khoiNutLenh.setVisibility(View.GONE);
                                }
                            }
                        },
                        throwable -> {
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

        themBaiKT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ThemBaiKiemTra.class);
                intent.putExtra("maKhoaHoc45", khoaHoc.getMakhoahoc());
                startActivity(intent);
            }
        });
    }

    private void anhXa() {
        avtKhoaHoc = findViewById(R.id.hinhKhoaHoc_LBKT);
        back = findViewById(R.id.backLBKT);

        tenGiangVien = findViewById(R.id.tenGV_LBKT);
        tenKhoaHoc = findViewById(R.id.tenKhoaHoc_LBKT);

        khoiNutLenh = findViewById(R.id.khoiNutLenh_ThemBaiKiemTra);
        themBaiKT = findViewById(R.id.themBaiKiemTra);
        noDataImage = findViewById(R.id.nodata_images_LBKT);

        recyclerViewBaiKT = findViewById(R.id.baiKiemTra_LBKT);
        mangBaiKiemTra = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerViewBaiKT.setLayoutManager(layoutManager);
        recyclerViewBaiKT.setHasFixedSize(true);

        dialogAppLoading = new DialogAppLoading(this);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.lobby_kiem_tra);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        getDuLieuBaiKT_RecyclerView();
        super.onResume();
    }
}