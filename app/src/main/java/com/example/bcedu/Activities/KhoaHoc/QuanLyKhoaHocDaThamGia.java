package com.example.bcedu.Activities.KhoaHoc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bcedu.Adapter.KhoaHocTheoGiangVienAdapter;
import com.example.bcedu.Adapter.KhoaHocTheoHocVienAdapter;
import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.Model.KhoaHoc;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QuanLyKhoaHocDaThamGia extends AppCompatActivity {
    private TextView ten, vaitro;
    private ShapeableImageView avt, hinhRank;
    private ImageView noDataImage;
    private List<KhoaHoc> mangKhoaHocDaThamGia;
    private RecyclerView recyclerViewKhoaHocDTG;
    private KhoaHocTheoHocVienAdapter adapter;
    private ShapeableImageView back;

    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private DialogAppLoading dialogAppLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        anhXa();
        dialogAppLoading.show();

        getKhoaHocTheoMaHocVien();



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getKhoaHocTheoMaHocVien() {
        compositeDisposable.add(apiHocTap.getKhoaHocTheoMaHocVien_API(Utils.nguoiDungCurrent.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        khoaHocModel -> {
                            if (khoaHocModel.isSuccess()){
                                mangKhoaHocDaThamGia = khoaHocModel.getResult();
                                adapter = new KhoaHocTheoHocVienAdapter(mangKhoaHocDaThamGia, this);
                                recyclerViewKhoaHocDTG.setAdapter(adapter);

                                dialogAppLoading.cancel();

                                if (mangKhoaHocDaThamGia.size() > 0)
                                {
                                    noDataImage.setVisibility(View.INVISIBLE);
                                }
                            }
                            else {
                                dialogAppLoading.cancel();
                                StyleableToast.makeText(this, "Bạn chưa tham gia khoá học nào!", R.style.warning).show();
                            }
                        }, throwable -> {
                            dialogAppLoading.cancel();
                            StyleableToast.makeText(this, "Không kết nối được với server!", R.style.error_toast).show();
                            Log.e("logTVTCD", "AAA: " + throwable.getMessage());
                        }
                ));
    }

    private void anhXa() {
        ten = findViewById(R.id.tenGiangVien_QLKH_DTG);
        vaitro = findViewById(R.id.vaiTro_QLKH_DTG);
        avt = findViewById(R.id.hinhNguoiDung_QLKH_DTG);
        hinhRank = findViewById(R.id.hinhRankNguoiDung_QLKH_DTG);
        noDataImage = findViewById(R.id.nodata_images_DTG);

        back = findViewById(R.id.back_DTG);

        ten.setText(Utils.nguoiDungCurrent.getTennguoidung().trim());
        vaitro.setText(Utils.nguoiDungCurrent.getVaitro().trim());

        Glide.with(getApplicationContext()).load(Utils.xepHangCuaToi.getHinhanh()).into(hinhRank);
        Glide.with(getApplicationContext()).load(Utils.BASE_URL + "images/" +Utils.nguoiDungCurrent.getAnhdaidien()).into(avt);

        recyclerViewKhoaHocDTG = findViewById(R.id.recyclerView_KH_DTG);
        mangKhoaHocDaThamGia = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerViewKhoaHocDTG.setLayoutManager(layoutManager);
        recyclerViewKhoaHocDTG.setHasFixedSize(true);

        dialogAppLoading = new DialogAppLoading(this);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quan_ly_khoa_hoc_da_tham_gia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}