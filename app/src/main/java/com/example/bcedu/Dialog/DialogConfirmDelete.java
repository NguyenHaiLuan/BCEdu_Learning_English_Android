package com.example.bcedu.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.bcedu.Interface.DialogConfirmDeleteListener;
import com.example.bcedu.R;

public class DialogConfirmDelete extends Dialog {
    private LottieAnimationView xoaAnimation;
    private Button xoa, khongXoa;
    private DialogConfirmDeleteListener listener;
    public DialogConfirmDelete(@NonNull Context context, DialogConfirmDeleteListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_confirm_delete);
        // Tìm thấy LottieAnimationView trong layout
        xoaAnimation = findViewById(R.id.lottie_animation_view_xoa);
        // Đặt file json cho animation
        xoaAnimation.setAnimation(R.raw.delete);

        xoa = findViewById(R.id.xoaDialog);
        khongXoa = findViewById(R.id.khongXoaDialog);
        // Khởi chạy animation
        xoaAnimation.playAnimation();

        // Đặt chế độ lặp lại cho animation thành vô hạn
        xoaAnimation.setRepeatCount(LottieDrawable.INFINITE);

        khongXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteConfirmed();
                }
                dismiss();
            }
        });
    }
}
