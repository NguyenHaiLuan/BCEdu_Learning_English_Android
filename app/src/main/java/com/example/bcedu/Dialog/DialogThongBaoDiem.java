package com.example.bcedu.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.bcedu.Activities.KiemTra.KiemTraTracNghiem;
import com.example.bcedu.R;

public class DialogThongBaoDiem extends Dialog {
    private Button ok;
    private KiemTraTracNghiem kiemTraTracNghiem;
    private float diem;
    private TextView diemText;

    public DialogThongBaoDiem(@NonNull Context context, KiemTraTracNghiem kiemTraTracNghiem, float diem) {
        super(context);
        this.kiemTraTracNghiem = kiemTraTracNghiem;
        this.diem = diem;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_thong_bao_diem);

        // Tìm thấy LottieAnimationView trong layout
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottie_animation_Diem);

        // Đặt file json cho animation
        lottieAnimationView.setAnimation(R.raw.cute_animation);

        // Đặt chế độ lặp lại cho animation thành vô hạn
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);

        // Khởi chạy animation
        lottieAnimationView.playAnimation();
        ok = findViewById(R.id.okBtn_Dialog);
        diemText = findViewById(R.id.diemDialog);

        diemText.setText(""+diem);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
