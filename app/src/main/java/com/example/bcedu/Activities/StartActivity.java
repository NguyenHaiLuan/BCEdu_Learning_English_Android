package com.example.bcedu.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.bcedu.R;

public class StartActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_TIMEOUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tìm thấy LottieAnimationView trong layout
        LottieAnimationView lottieAnimationView = findViewById(R.id.loadingAnim);

        // Đặt file json cho animation
        lottieAnimationView.setAnimation(R.raw.loading);

        // Đặt chế độ lặp lại cho animation thành vô hạn
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);

        // Khởi chạy animation
        lottieAnimationView.playAnimation();



        // Sử dụng Handler để trì hoãn chuyển đổi Activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Chuyển sang Activity khác
                Intent intent = new Intent(StartActivity.this, SplashScreen.class);
                startActivity(intent);
                finish(); // Đóng Activity hiện tại
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }
}