package com.example.bcedu.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.bcedu.Activities.KiemTra.KiemTraTracNghiem;
import com.example.bcedu.R;

import io.github.muddz.styleabletoast.StyleableToast;

public class DialogTimeOver_KT extends Dialog {
    private KiemTraTracNghiem kiemTraTracNghiem;
    public DialogTimeOver_KT(@NonNull Context context, KiemTraTracNghiem kiemTraTracNghiem) {
        super(context);
        this.kiemTraTracNghiem = kiemTraTracNghiem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_het_thoi_gian);

        Button retryButton = findViewById(R.id.goTimeOVerBtn);
        Button exitButton = findViewById(R.id.thoatBtnTimeOver);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StyleableToast.makeText(kiemTraTracNghiem, "Cố gắng hết sức bạn nhé!", Toast.LENGTH_SHORT ,R.style.success_toast).show();
                DialogTimeOver_KT.this.dismiss();
                kiemTraTracNghiem.recreate();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kiemTraTracNghiem.finish();
            }
        });
    }
}
