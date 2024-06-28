package com.example.bcedu.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Service.MusicService;
import com.example.bcedu.Utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Setting extends AppCompatActivity {
    private Switch soundSwitch;
    private boolean batNhac;
    private ApiHocTap apiHocTap;
    private ImageButton back;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        soundSwitch = findViewById(R.id.soundSwitch);
        back = findViewById(R.id.backSetting);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (Utils.nguoiDungCurrent.getAmthanh() == 0)
            batNhac = false;
        else
            batNhac = true;

        if (batNhac) {
            soundSwitch.setChecked(true);
            startMusicService();
        } else {
            soundSwitch.setChecked(false);
            stopMusicService();
        }

        soundSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                batNhac = !batNhac;

                if (batNhac) {
                    setBatNhac(1);
                } else {
                    setBatNhac(0);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void setBatNhac(int trangthai) {
        if (trangthai == 1) {
            startMusicService();
        } else {
            stopMusicService();
        }

        // Cập nhật trạng thái âm thanh trên máy chủ
        compositeDisposable.add(apiHocTap.caiDatNhac_API(Utils.nguoiDungCurrent.getManguoidung(), trangthai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        caiNhacModel -> {
                            if (caiNhacModel.isSuccess()) {
                                // Cập nhật trạng thái thành công
                            } else {
                                Log.d("log_CTTV1", caiNhacModel.getMessage());
                            }
                        }, throwable -> {
                            Log.d("log_CTTV", throwable.getMessage());
                        }
                ));
    }

    private void startMusicService() {
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        startService(intent);
    }

    private void stopMusicService() {
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        stopService(intent);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
