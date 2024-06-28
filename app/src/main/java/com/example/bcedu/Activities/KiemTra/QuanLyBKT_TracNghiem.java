package com.example.bcedu.Activities.KiemTra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.bcedu.Adapter.BaiTracNghiemAdapter;
import com.example.bcedu.Model.BaiKiemTra;
import com.example.bcedu.Model.CauTracNghiem;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QuanLyBKT_TracNghiem extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BaiTracNghiemAdapter adapter;
    private ApiHocTap apiHocTap;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ImageButton back, them;
    private TextView txtThongtin;
    private List<CauTracNghiem> mangCauTracNghiem;
    private LottieAnimationView animationView;
    private BaiKiemTra baiKiemTra = new BaiKiemTra();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        anhXa();
        initAnimation();
        baiKiemTra = (BaiKiemTra) getIntent().getSerializableExtra("baiKiemTraCS");

        if (Utils.isConnected(this)) {
            getDuLieuTracNghiem();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ThemCauTracNghiem.class);
                intent.putExtra("BaiKiemTra_QL", baiKiemTra);
                startActivity(intent);
            }
        });
    }

    private void initAnimation() {
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottie_animation_view_nodata_TN);

        // Đặt file json cho animation
        lottieAnimationView.setAnimation(R.raw.nodata);

        // Đặt chế độ lặp lại cho animation thành vô hạn
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);

        // Khởi chạy animation
        lottieAnimationView.playAnimation();

    }

    private void getDuLieuTracNghiem() {
        compositeDisposable.add(apiHocTap.getCauHoiTracNghiem(baiKiemTra.getMabaikiemtra())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        khoaHocModel -> {
                            if (khoaHocModel.isSuccess()) {
                                mangCauTracNghiem = khoaHocModel.getResult();
                                adapter = new BaiTracNghiemAdapter(mangCauTracNghiem, this);
                                recyclerView.setAdapter(adapter);

                                if (mangCauTracNghiem.size() > 0) {
                                    animationView.setVisibility(View.INVISIBLE);
                                    txtThongtin.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, throwable -> {
                            StyleableToast.makeText(this, "Không kết nối được với server!", R.style.error_toast).show();
                            Log.e("logTVTCD", "AAA: " + throwable.getMessage());
                        }
                ));
    }

    private void anhXa() {
        back = findViewById(R.id.back_TN);
        them = findViewById(R.id.themTn);
        txtThongtin = findViewById(R.id.textGioiThieu_TN);
        animationView = findViewById(R.id.lottie_animation_view_nodata_TN);

        recyclerView = findViewById(R.id.recyclerView_cauTN);
        mangCauTracNghiem = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }


    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quan_ly_bkt_trac_nghiem);
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
        getDuLieuTracNghiem();
        super.onResume();
    }
}