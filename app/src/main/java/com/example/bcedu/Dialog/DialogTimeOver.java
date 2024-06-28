package com.example.bcedu.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.bcedu.Activities.TuVung.OnTapTuVung;
import com.example.bcedu.R;

import io.github.muddz.styleabletoast.StyleableToast;

public class DialogTimeOver extends Dialog {
    private OnTapTuVung onTapTuVung;

    public DialogTimeOver(@NonNull Context context, OnTapTuVung onTapTuVung) {
        super(context);
        this.onTapTuVung = onTapTuVung;
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
               StyleableToast.makeText(onTapTuVung, "Cố gắng hết sức bạn nhé!", Toast.LENGTH_SHORT ,R.style.success_toast).show();
               DialogTimeOver.this.dismiss();
               onTapTuVung.recreate();
           }
       });

       exitButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onTapTuVung.finish();
           }
       });
    }
}


