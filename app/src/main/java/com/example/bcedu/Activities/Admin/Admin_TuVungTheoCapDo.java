package com.example.bcedu.Activities.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bcedu.Adapter.KhoaHocTheoGiangVienAdapter;
import com.example.bcedu.Adapter.TuVungAdminAdapter;
import com.example.bcedu.Model.CapDoTuVung;
import com.example.bcedu.Model.TuVung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Admin_TuVungTheoCapDo extends AppCompatActivity {
    private List<TuVung> mangTuVung;
    private RecyclerView recyclerViewTuVung;
    ApiHocTap apiHocTap;
    private ShapeableImageView themtuvung;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private TuVungAdminAdapter adapter;
    private CapDoTuVung capDoTuVung = new CapDoTuVung();
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        anhXa();

        capDoTuVung = (CapDoTuVung) getIntent().getSerializableExtra("capdotuvung_admin");
        getDuLieuCapDoTuVung();

        eventClick();
    }

    private void eventClick() {
        themtuvung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Admin_ThemTuVung.class);
                intent.putExtra("capdo2412", capDoTuVung);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getDuLieuCapDoTuVung() {
        compositeDisposable.add(apiHocTap.getTuVungAPI(capDoTuVung.getMacapdo())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model -> {
                            if (model.isSuccess()) {
                                mangTuVung = model.getResult();
                                adapter = new TuVungAdminAdapter(mangTuVung, this);
                                recyclerViewTuVung.setAdapter(adapter);
                            }
                        },
                        throwable -> {
                            Log.e("Loi", "get_xephang: " + throwable.getMessage());
                        }
                ));
    }

    private void anhXa() {
        recyclerViewTuVung = findViewById(R.id.recyclerView_tuvung_admin);
        mangTuVung = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerViewTuVung.setLayoutManager(layoutManager);
        recyclerViewTuVung.setHasFixedSize(true);

        themtuvung = findViewById(R.id.them_tu_vung_admin);
        back = findViewById(R.id.back_TV_ADMIN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDuLieuCapDoTuVung();
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_tu_vung_theo_cap_do);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}