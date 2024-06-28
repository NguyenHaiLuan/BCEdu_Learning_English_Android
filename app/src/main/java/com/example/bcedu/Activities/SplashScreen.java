package com.example.bcedu.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bcedu.Activities.NguoiDung.SignIn;
import com.example.bcedu.R;

public class SplashScreen extends AppCompatActivity {
    private TextView textLogo, textSlogan, developedText;
    private Handler handler = new Handler();
    private Runnable runnable;
    private LottieAnimationView animationSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        anhXa();

        // Nếu có ket noi internet
        Animation slideInFromLeft = AnimationUtils.loadAnimation(this, R.anim.slide_from_left);

        // Áp dụng animation vào TextView
        textLogo.startAnimation(slideInFromLeft);
        textSlogan.startAnimation(slideInFromLeft);
        developedText.startAnimation(slideInFromLeft);

        // khởi chạy animation cat coding
        animationSplash.playAnimation();

        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, SignIn.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable, 3300);
    }

    private void anhXa() {
        textLogo = findViewById(R.id.text_logo);
        textSlogan = findViewById(R.id.text_slogan);
        developedText = findViewById(R.id.developText);
        animationSplash = findViewById(R.id.animationSplash);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


}