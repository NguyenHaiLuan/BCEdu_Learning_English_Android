package com.example.bcedu.Activities.TuVung;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bcedu.Adapter.TuVungAdapter;
import com.example.bcedu.Model.TuVung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Service.MusicService;
import com.example.bcedu.Utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TuVungTheoCapDo extends AppCompatActivity {
    private Toolbar toolbar;
    private List<TuVung> mangTuVung;
    private RecyclerView recyclerViewTuVung;
    private ImageButton kiemTra, back;
    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int macapdotuvung;

    TuVungAdapter tuVungAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        macapdotuvung = getIntent().getIntExtra("macapdoIntent", 1) + 1;

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        anhXa();

        getTuVungTheoCapDo();
        kiemTra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mangTuVung != null && !mangTuVung.isEmpty()) {
                    Intent intent = new Intent(TuVungTheoCapDo.this, OnTapTuVung.class);
                    intent.putExtra("danhSachTuVungA", (Serializable) mangTuVung);
                    // Truyền 1 số để biết đây là từ TVTCD qua
                    int soXacNhan = 69;
                    intent.putExtra("soXacNhan", soXacNhan);
                    startActivity(intent);
                } else {
                    StyleableToast.makeText(TuVungTheoCapDo.this, "Không có từ vựng để ôn tập!", R.style.warning).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getTuVungTheoCapDo() {
        compositeDisposable.add(apiHocTap.getTuVungAPI(macapdotuvung)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tuVungModel -> {
                            if (tuVungModel.isSuccess()){
                                mangTuVung = tuVungModel.getResult();
                                tuVungAdapter = new TuVungAdapter(mangTuVung, this);
                                recyclerViewTuVung.setAdapter(tuVungAdapter);
                            }
                        }, throwable ->{
                            StyleableToast.makeText(this, "Không kết nối được với server!", R.style.error_toast).show();
                            Log.e("logTVTCD", "AAA: " + throwable.getMessage());
                        }
                ));
    }

    private void anhXa() {
        recyclerViewTuVung = findViewById(R.id.recyclerView_TuVung);
        mangTuVung = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewTuVung.setLayoutManager(layoutManager);
        recyclerViewTuVung.setHasFixedSize(true);
        kiemTra = findViewById(R.id.kiemTraTuVungTheoCapDo);
        back = findViewById(R.id.backTVTCD_Btn);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tu_vung_theo_cap_do);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void reloadDuLieu(){
        compositeDisposable.add(apiHocTap.dangNhapAPI(Utils.nguoiDungCurrent.getEmail(), Utils.nguoiDungCurrent.getMatkhau())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dangNhapHocVienModel -> {
                            if (dangNhapHocVienModel.isSuccess()){
                                Utils.nguoiDungCurrent = dangNhapHocVienModel.getResult().get(0);
                                getBacRank();
                                getBacRankTiepTheo();
                            }
                            else {
                                StyleableToast.makeText(getApplicationContext(), dangNhapHocVienModel.getMessage(), R.style.error_toast).show();
                                Log.d("log_CTTV1", dangNhapHocVienModel.getMessage());
                            }
                        }, throwable ->{
                            Log.d("log_CTTV", throwable.getMessage());
                        }
                ));
    }

    private void getBacRank() {
        compositeDisposable.add(apiHocTap.getXepHangAPI(Utils.nguoiDungCurrent.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model2 -> {
                            if (model2.isSuccess()) {
                                Utils.xepHangCuaToi = model2.getResult().get(0);
                            } else {
                            }
                        }, throwable -> {
                            Log.e("Loi", "get_xephang: " + throwable.getMessage());
                        }
                ));
    }
    private void getBacRankTiepTheo() {
        compositeDisposable.add(apiHocTap.getXepHangMoiAPI(Utils.nguoiDungCurrent.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model3 -> {
                            if (model3.isSuccess()) {
                                Utils.xepHangTiepTheo = model3.getResult().get(0);
                            } else {
                            }
                        }, throwable -> {
                            Log.e("Loi", "get_xephang2: " + throwable.getMessage());
                        }
                ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);
        reloadDuLieu();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}