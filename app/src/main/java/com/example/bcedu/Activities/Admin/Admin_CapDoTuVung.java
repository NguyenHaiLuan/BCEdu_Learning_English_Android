package com.example.bcedu.Activities.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bcedu.Activities.TuVung.TuVungTheoCapDo;
import com.example.bcedu.Adapter.CapDoTuVungAdapter;
import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.Model.CapDoTuVung;
import com.example.bcedu.Model.KetQuaOnTap;
import com.example.bcedu.Model.TuVung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Admin_CapDoTuVung extends AppCompatActivity {
    private ListView listViewCapDoTuVung;
    private List<CapDoTuVung> mangCapDoTuVung;
    private CapDoTuVungAdapter capDoTuVungAdapter;
    private ImageButton reloadBtn, back;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiHocTap apiHocTap;
    private DialogAppLoading dialogAppLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        anhXa();

        if (Utils.isConnected(this)) {
            getCDTV();
        } else {
            StyleableToast.makeText(this, "Không có Internet!", R.style.error_toast).show();
        }

        listViewCapDoTuVung.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent level = new Intent(getApplicationContext(), Admin_TuVungTheoCapDo.class);
                CapDoTuVung capDoTuVung = new CapDoTuVung();
                capDoTuVung = mangCapDoTuVung.get(position);
                level.putExtra("capdotuvung_admin", capDoTuVung);
                startActivity(level);
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCDTV();
                dialogAppLoading.show();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        dialogAppLoading.cancel();
                        StyleableToast.makeText(getApplicationContext(), "Đã cập nhật danh sách từ vựng thành công!", R.style.success_toast).show();
                    }
                };
                handler.postDelayed(runnable, 2000);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getCDTV() {
        compositeDisposable.add(apiHocTap.getCapDoTuVungAPI()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        capDoTuVungModel -> {
                            if (capDoTuVungModel.isSuccess()) {
                                mangCapDoTuVung = capDoTuVungModel.getResult();
                                capDoTuVungAdapter = new CapDoTuVungAdapter(mangCapDoTuVung, this);
                                listViewCapDoTuVung.setAdapter(capDoTuVungAdapter);
                            }
                        },
                        throwable -> {
                            // Xử lý ngoại lệ ở đây
                            Log.e("logCDTV", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                        }
                ));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCDTV();
        reloadDuLieu();
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

    private void reloadDuLieu() {
        compositeDisposable.add(apiHocTap.dangNhapAPI(Utils.nguoiDungCurrent.getEmail(), Utils.nguoiDungCurrent.getMatkhau())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dangNhapHocVienModel -> {
                            if (dangNhapHocVienModel.isSuccess()) {
                                Utils.nguoiDungCurrent = dangNhapHocVienModel.getResult().get(0);
                                getBacRank();
                                getBacRankTiepTheo();
                            } else {
                                StyleableToast.makeText(getApplicationContext(), dangNhapHocVienModel.getMessage(), R.style.error_toast).show();
                                Log.d("log_CTTV1", dangNhapHocVienModel.getMessage());
                            }
                        }, throwable -> {
                            Log.d("log_CTTV", throwable.getMessage());
                        }
                ));
    }

    private void anhXa() {
        listViewCapDoTuVung = findViewById(R.id.listview_capdo_tuvung_admin);
        mangCapDoTuVung = new ArrayList<>();

        reloadBtn = findViewById(R.id.reloadBtn_admin);

        dialogAppLoading = new DialogAppLoading(this);
        back = findViewById(R.id.back_CDTV_admin);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_cap_do_tu_vung);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}