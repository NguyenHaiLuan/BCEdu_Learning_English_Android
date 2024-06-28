package com.example.bcedu.Activities.KiemTra;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.example.bcedu.Model.BaiKiemTra;
import com.example.bcedu.Model.KetQuaKiemTra;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GioiThieuBaiKiemTra extends AppCompatActivity {
    private BaiKiemTra baiKiemTra = new BaiKiemTra();
    private TextView ten, soCauHoi, thoiLuong, diemCaoNhat;
    private ShapeableImageView hinh;
    private Button batdau;
    private KetQuaKiemTra ketquaKT = new KetQuaKiemTra();
    private float diemHV;
    private ApiHocTap apiHocTap;
    private CompositeDisposable compositeDisposable =new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        anhXa();

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);


        if (Utils.isConnected(getApplicationContext())){
            getDuLieuBaiKiemTra();
        }
        else{
            StyleableToast.makeText(getApplicationContext(), "Không có kết nối Internet", R.style.error_toast).show();
        }

        // Lấy thông tin về bài kiểm tra
        getDuLieuBaiKiemTra();

        // Lấy thông tin điểm của học viên
        getDuLieuDiemHocVien();

        initGaAnimation();

        batdau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), KiemTraTracNghiem.class);
                    intent.putExtra("BaiKiemTra_GT", baiKiemTra);
                    intent.putExtra("diemHT69", ketquaKT);
                    startActivity(intent);
            }
        });
    }
    private void getDuLieuDiemHocVien() {
        compositeDisposable.add(apiHocTap.getDiemKiemTra_API(Utils.nguoiDungCurrent.getManguoidung(), baiKiemTra.getMabaikiemtra())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        baiKiemTraModel -> {
                            if (baiKiemTraModel.isSuccess() && baiKiemTraModel.getResult() != null && !baiKiemTraModel.getResult().isEmpty()) {
                                ketquaKT = baiKiemTraModel.getResult().get(0);
                                diemHV = ketquaKT.getDiemso();
                                diemCaoNhat.setText("Điểm cao nhất: " + diemHV);
                            } else {
                                Log.d("Loi", "Không tìm thấy điểm của học viên hoặc kết quả trống!");
                            }
                        }, throwable -> {
                            Log.e("KTTN getList câu trắc nghiệm:", "AAA: " + throwable.getMessage());
                        }
                ));
    }


    private void anhXa() {
        ten = findViewById(R.id.tenbaikiemtra_start);
        soCauHoi = findViewById(R.id.soCauHoiStart);
        thoiLuong = findViewById(R.id.thoiLuongStart);
        hinh = findViewById(R.id.hinhBaiKiemTra_Start);
        batdau = findViewById(R.id.batDau_start);
        diemCaoNhat = findViewById(R.id.diemcaonhatStart);
    }

    private void getDuLieuBaiKiemTra() {
        baiKiemTra = (BaiKiemTra) getIntent().getSerializableExtra("chiTietBKT");

        ten.setText(baiKiemTra.getTenbaikiemtra().toString().trim());
        soCauHoi.setText("Số câu hỏi: "+baiKiemTra.getSocauhoi());
        thoiLuong.setText("Thời lượng: "+baiKiemTra.getThoiluong()+" phút");

        Glide.with(getApplicationContext()).load(Utils.BASE_URL + "images/" + baiKiemTra.getHinhanhminhhoa()).into(hinh);
    }

    private void initGaAnimation() {
        // Tìm thấy LottieAnimationView trong layout
        LottieAnimationView lottieAnimationView = findViewById(R.id.gaAnimation);

        // Đặt file json cho animation
        lottieAnimationView.setAnimation(R.raw.ga);

        // Đặt chế độ lặp lại cho animation thành vô hạn
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);

        // Khởi chạy animation
        lottieAnimationView.playAnimation();

        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diemHV < 100){
                    StyleableToast.makeText(getApplicationContext(), "Gà ơi là gà!" ,Toast.LENGTH_LONG,R.style.thongBaoDiem).show();
                    finish();
                }
                else {
                    StyleableToast.makeText(getApplicationContext(), "Ngầu liền!" ,Toast.LENGTH_LONG,R.style.thongBaoDiem).show();
                    finish();
                }
            }
        });
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gioi_thieu_bai_kt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        reloadDuLieu();
        super.onResume();
    }

    private void reloadDuLieu() {
        getDuLieuBaiKiemTra();
        getDuLieuDiemHocVien();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}