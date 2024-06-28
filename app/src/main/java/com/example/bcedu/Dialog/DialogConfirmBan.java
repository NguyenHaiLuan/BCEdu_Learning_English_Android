package com.example.bcedu.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.bcedu.Interface.DialogConfirmBanListener;
import com.example.bcedu.R;

public class DialogConfirmBan extends Dialog {
    private LottieAnimationView xoaAnimation;
    private Button ban, khongBan;
    private DialogConfirmBanListener listener;
    public DialogConfirmBan(@NonNull Context context, DialogConfirmBanListener listener) {
        super(context);
        this.listener = listener;

        setContentView(R.layout.dialog_confirm_ban);
        // Tìm thấy LottieAnimationView trong layout
        xoaAnimation = findViewById(R.id.lottie_animation_view_ban);
        // Đặt file json cho animation
        xoaAnimation.setAnimation(R.raw.block);

        ban = findViewById(R.id.banDialog);
        khongBan = findViewById(R.id.khongBanDialog);
        // Khởi chạy animation
        xoaAnimation.playAnimation();

        // Đặt chế độ lặp lại cho animation thành vô hạn
        xoaAnimation.setRepeatCount(LottieDrawable.INFINITE);

        khongBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBanConfirmed();
                }
                dismiss();
            }
        });
    }
}
