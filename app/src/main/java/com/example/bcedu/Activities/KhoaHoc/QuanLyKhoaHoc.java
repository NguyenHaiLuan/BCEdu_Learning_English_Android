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
import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.Model.BaiKiemTra;
import com.example.bcedu.Model.KhoaHoc;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QuanLyKhoaHoc extends AppCompatActivity {
    private TextView ten, vaitro, soKhoaHoc, soHocVien,soBaiKiemTra;
    private ShapeableImageView avt, hinhRank;
    private List<KhoaHoc> mangKhoaHocDaTao;
    private List<BaiKiemTra> mangBaiKiemTra;
    private RecyclerView recyclerViewKhoaHocDaTao;
    private KhoaHocTheoGiangVienAdapter adapter;
    private ShapeableImageView themKH, back;
    private ImageView noDataImage;
    private DialogAppLoading dialogAppLoading;

    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        anhXa();
        dialogAppLoading.show();

        getKhoaHocTheoMaGiangVien();
        getBaiKiemTraTheoMaGV();

        themKH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ThemKhoaHoc.class);
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

    private void getBaiKiemTraTheoMaGV() {
        compositeDisposable.add(apiHocTap.getBaiKiemTraTheoNguoiDung(Utils.nguoiDungCurrent.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        baiKiemTraModel -> {
                            if (baiKiemTraModel.isSuccess()){
                                mangBaiKiemTra = baiKiemTraModel.getResult();
                                soBaiKiemTra.setText(""+mangBaiKiemTra.size());
                            }
                        }, throwable -> {
                            StyleableToast.makeText(this, "Không kết nối được với server!", R.style.error_toast).show();
                            Log.e("logTVTCD", "AAA: " + throwable.getMessage());
                        }
                ));
    }

    private void getKhoaHocTheoMaGiangVien() {
        compositeDisposable.add(apiHocTap.getKhoaHocTheoMaGiangVien_API(Utils.nguoiDungCurrent.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        khoaHocModel -> {
                            if (khoaHocModel.isSuccess()){
                                dialogAppLoading.cancel();
                                mangKhoaHocDaTao = khoaHocModel.getResult();
                                adapter = new KhoaHocTheoGiangVienAdapter(mangKhoaHocDaTao, this);
                                recyclerViewKhoaHocDaTao.setAdapter(adapter);

                                if (mangKhoaHocDaTao.isEmpty()) {
                                    // Nếu danh sách rỗng, cập nhật ngay giá trị cho TextView
                                    soKhoaHoc.setText("0");
                                    soHocVien.setText("0");
                                } else {
                                    // Nếu danh sách không rỗng, tiến hành tính tổng số học viên
                                    getTongSoHocVien(mangKhoaHocDaTao);
                                    // Cập nhật số lượng khóa học
                                    soKhoaHoc.setText(String.valueOf(mangKhoaHocDaTao.size()));
                                }

                                if (mangKhoaHocDaTao.size() > 0)
                                {
                                    noDataImage.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                dialogAppLoading.cancel();
                            }
                        }, throwable -> {
                            dialogAppLoading.cancel();
                            StyleableToast.makeText(this, "Không kết nối được với server!", R.style.error_toast).show();
                            Log.e("logTVTCD", "AAA: " + throwable.getMessage());
                        }
                ));
    }

    private void getTongSoHocVien(List<KhoaHoc> array) {
        AtomicInteger tongSoKhoaHoc = new AtomicInteger(array.size());
        int[] soHocVienCount = {0};

        for (KhoaHoc khoaHoc : array) {
            compositeDisposable.add(apiHocTap.getDanhSachThamGiaKhoaHocAPI(khoaHoc.getMakhoahoc())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            danhSachHocVienModel -> {
                                if (danhSachHocVienModel.isSuccess()) {
                                    // Tính tổng số học viên từ mỗi danh sách trả về
                                    soHocVienCount[0] += danhSachHocVienModel.getResult().size();
                                } else {
                                    Log.d("LoiLoadDuLieuHocVien", danhSachHocVienModel.getMessage());
                                }

                                // Giảm số lượng khóa học đang chờ xử lý
                                if (tongSoKhoaHoc.decrementAndGet() == 0) {
                                    // Cập nhật TextView sau khi tất cả các yêu cầu hoàn thành
                                    soHocVien.setText(String.valueOf(soHocVienCount[0]));
                                }
                            },
                            throwable -> {
                                Log.e("Error", "Không kết nối được với server! Lỗi: " + throwable.getMessage());

                                // Giảm số lượng khóa học đang chờ xử lý
                                if (tongSoKhoaHoc.decrementAndGet() == 0) {
                                    // Cập nhật TextView sau khi tất cả các yêu cầu hoàn thành
                                    soHocVien.setText(String.valueOf(soHocVienCount[0]));
                                }
                            }
                    ));
        }
    }

    private void anhXa() {
        ten = findViewById(R.id.tenGiangVien_QLKH);
        vaitro = findViewById(R.id.vaiTro_QLKH);
        avt = findViewById(R.id.hinhNguoiDung_QLKH);
        hinhRank = findViewById(R.id.hinhRankNguoiDung_QLKH);
        noDataImage = findViewById(R.id.nodata_images_QLKH);
        back = findViewById(R.id.back_QLKH);

        soKhoaHoc = findViewById(R.id.soKhoaHoc_QLKH);
        soHocVien = findViewById(R.id.soHocVien_QLKH);
        themKH = findViewById(R.id.them_KH);
        soBaiKiemTra = findViewById(R.id.soBaiKiemTra_GV);

        ten.setText(Utils.nguoiDungCurrent.getTennguoidung().trim());
        vaitro.setText(Utils.nguoiDungCurrent.getVaitro().trim());

        Glide.with(getApplicationContext()).load(Utils.xepHangCuaToi.getHinhanh()).into(hinhRank);
        Glide.with(getApplicationContext()).load(Utils.BASE_URL + "images/" +Utils.nguoiDungCurrent.getAnhdaidien()).into(avt);

        recyclerViewKhoaHocDaTao = findViewById(R.id.recyclerView_KH_QLKH);
        mangKhoaHocDaTao = new ArrayList<>();
        mangBaiKiemTra = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerViewKhoaHocDaTao.setLayoutManager(layoutManager);
        recyclerViewKhoaHocDaTao.setHasFixedSize(true);

        dialogAppLoading = new DialogAppLoading(this);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quan_ly_khoa_hoc);
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

    @Override
    protected void onResume() {
        super.onResume();
        getKhoaHocTheoMaGiangVien();
        getBaiKiemTraTheoMaGV();
    }
}