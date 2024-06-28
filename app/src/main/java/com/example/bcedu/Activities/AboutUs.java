package com.example.bcedu.Activities;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.bcedu.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

public class AboutUs extends AppCompatActivity {
    boolean mothu = true;
    private ShapeableImageView bucthu;
    private CardView noidungthu;
    private LottieAnimationView chongchong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        anhXa();
        Animation slideInFromBottom = AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom);

        // Áp dụng animation vào TextView
        bucthu.startAnimation(slideInFromBottom);

        khoiTaoAnimation();

        bucThuClickEvent();

    }

    private void bucThuClickEvent() {
        bucthu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mothu = !mothu; // Đảo ngược trạng thái hiện tại
                if (mothu) {
                    // Hiển thị mật khẩu
                    noidungthu.setVisibility(View.VISIBLE);
                    bucthu.setImageResource(R.drawable.email_open);
                } else {
                    // Ẩn mật khẩu
                    noidungthu.setVisibility(View.INVISIBLE);
                    bucthu.setImageResource(R.drawable.email_close);
                }
            }
        });
    }

    private void khoiTaoAnimation() {
        // Đặt file json cho animation
        chongchong.setAnimation(R.raw.thanks);
        // Đặt chế độ lặp lại cho animation thành vô hạn
        chongchong.setRepeatCount(LottieDrawable.INFINITE);
        // Khởi chạy animation
        chongchong.playAnimation();
    }

    private void anhXa() {
        bucthu = findViewById(R.id.bucthu);
        chongchong = findViewById(R.id.chongchong);
        noidungthu = findViewById(R.id.thu);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_us);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}