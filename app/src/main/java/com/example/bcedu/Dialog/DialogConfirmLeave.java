package com.example.bcedu.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.bcedu.Interface.DialogConfirmDeleteListener;
import com.example.bcedu.Interface.DialogConfirmLeaveListener;
import com.example.bcedu.R;

public class DialogConfirmLeave extends Dialog {
    private LottieAnimationView warningAnimation;
    private Button leave, dontLeave;
    private DialogConfirmLeaveListener listener;
    public DialogConfirmLeave(@NonNull Context context, DialogConfirmLeaveListener listener) {
        super(context);
        this.listener = listener;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_confirm_leave);
        // Tìm thấy LottieAnimationView trong layout
        warningAnimation = findViewById(R.id.lottie_animation_view_roi);

        // Đặt file json cho animation
        warningAnimation.setAnimation(R.raw.cute_animation);

        leave = findViewById(R.id.roiDialog);
        dontLeave = findViewById(R.id.khongRoiDialog);
        // Khởi chạy animation
        warningAnimation.playAnimation();

        // Đặt chế độ lặp lại cho animation thành vô hạn
        warningAnimation.setRepeatCount(LottieDrawable.INFINITE);

        dontLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onLeaveConfirmed();
                }
                dismiss();
            }
        });
    }
}
