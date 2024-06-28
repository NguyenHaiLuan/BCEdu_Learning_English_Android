package com.example.bcedu.Activities.KhoaHoc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bcedu.R;
import com.example.bcedu.Utils.Utils;

import io.github.muddz.styleabletoast.StyleableToast;

public class LobbyKhoaHoc extends AppCompatActivity {

    private CardView khoaHocCuaToi, tatCaKhoaHoc, khoaHocDaTao;
    private ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        anhXa();
        Animation slideInFromLeft = AnimationUtils.loadAnimation(this, R.anim.slide_from_left);

        // Áp dụng animation vào TextView
        khoaHocDaTao.startAnimation(slideInFromLeft);
        tatCaKhoaHoc.startAnimation(slideInFromLeft);
        khoaHocCuaToi.startAnimation(slideInFromLeft);

        eventClick();

    }

    private void eventClick() {
        tatCaKhoaHoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), KhoaHocActivity.class);
                startActivity(intent);
                finish();
            }
        });

        khoaHocCuaToi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QuanLyKhoaHocDaThamGia.class);
                startActivity(intent);
                finish();
            }
        });

        khoaHocDaTao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if(Utils.nguoiDungCurrent.getVaitro().equalsIgnoreCase("Học viên")){
                    StyleableToast.makeText(getApplicationContext(), "Chỉ có Giảng Viên và Admin mới có quyền truy cập!", R.style.warning).show();

                    // Re-enable the button after the toast duration
                    int toastDuration = 2000; // 2 seconds for Toast.LENGTH_SHORT
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setEnabled(true);
                        }
                    }, toastDuration);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), QuanLyKhoaHoc.class);
                    startActivity(intent);
                    v.setEnabled(true);
                    finish();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void anhXa() {
        tatCaKhoaHoc = findViewById(R.id.tatCaKhoaHocCardView);
        khoaHocCuaToi = findViewById(R.id.khoaHocCuaToiCardView);
        khoaHocDaTao = findViewById(R.id.khoaHocDaTaoCardView);
        back = findViewById(R.id.backBtnKH);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.lobby_khoa_hoc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}