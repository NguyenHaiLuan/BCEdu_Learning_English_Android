package com.example.bcedu.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.bcedu.Activities.TuVung.OnTapTuVung;
import com.example.bcedu.R;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

public class DialogDanhHieu extends Dialog {
    private TextView ten, mucRank;
    private Button chapNhanBtn;
    private ShapeableImageView anhRank;
    private LottieAnimationView lottieAnimationView;
    private OnTapTuVung onTapTuVung;
    public DialogDanhHieu(@NonNull Context context, OnTapTuVung onTapTuVung) {
        super(context);
        this.onTapTuVung = onTapTuVung;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_danh_hieu_moi);

        // Tìm thấy LottieAnimationView trong layout
        lottieAnimationView = findViewById(R.id.rankframe);
        ten = findViewById(R.id.tenDialog);
        mucRank = findViewById(R.id.mucRankDialog);
        anhRank = findViewById(R.id.anhRankDialog);
        chapNhanBtn = findViewById(R.id.chapNhanBtn);

        // Đặt file json cho animation
        lottieAnimationView.setAnimation(R.raw.rank_frame);
        // Khởi chạy animation
        lottieAnimationView.playAnimation();

        ten.setText(Utils.nguoiDungCurrent.getTennguoidung().trim());
        mucRank.setText(Utils.xepHangTiepTheo.getTenxephang());
        Glide.with(getContext()).load(Utils.xepHangTiepTheo.getHinhanh()).into(anhRank);

        chapNhanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onTapTuVung.finish();
            }
        });
    }
}
