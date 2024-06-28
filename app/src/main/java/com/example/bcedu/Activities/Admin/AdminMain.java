package com.example.bcedu.Activities.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bcedu.R;

public class AdminMain extends AppCompatActivity {
    private RelativeLayout qltv, qlnd;
    private CardView tuvung, nguoidung;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        qltv = findViewById(R.id.qltv);
        qlnd = findViewById(R.id.qlnd);
        tuvung = findViewById(R.id.QL_QLTV_CardView);
        nguoidung = findViewById(R.id.QL_RANK_CardView);
        back = findViewById(R.id.backAdminMainBtn);

        Animation slideInFromLeft = AnimationUtils.loadAnimation(this, R.anim.slide_from_left);

        // Áp dụng animation vào TextView
        tuvung.startAnimation(slideInFromLeft);
        nguoidung.startAnimation(slideInFromLeft);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        qltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Admin_CapDoTuVung.class);
                startActivity(intent);
            }
        });

        qlnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QuanLyNguoiDung.class);
                startActivity(intent);
            }
        });
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}