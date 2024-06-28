package com.example.bcedu.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.bcedu.R;

import io.github.muddz.styleabletoast.StyleableToast;

public class DialogNoInternet extends Dialog {

    private Button retryBtn;
    public DialogNoInternet(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_no_internet_layout);

        // Tìm thấy LottieAnimationView trong layout
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottie_animation_view);

        // Đặt file json cho animation
        lottieAnimationView.setAnimation(R.raw.meo404_2);

        // Đặt chế độ lặp lại cho animation thành vô hạn
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);

        // Khởi chạy animation
        lottieAnimationView.playAnimation();

        retryBtn = findViewById(R.id.retry_btn_no_internet);

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected(getContext())){
                    dismiss();
                    StyleableToast.makeText(getContext(), "Đã kết nối với Internet!", R.style.success_toast);

                } else {
                    StyleableToast.makeText(getContext(), "Vẫn chưa có kết nối internet!", R.style.error_toast).show();
                }
            }
        });
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                if (capabilities != null) {
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
            }
        }
        return false;
    }
}
