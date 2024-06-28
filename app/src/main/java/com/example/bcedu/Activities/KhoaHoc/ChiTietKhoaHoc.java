package com.example.bcedu.Activities.KhoaHoc;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.bcedu.Activities.KhoaHoc.TrangChuKhoaHoc;
import com.example.bcedu.Adapter.NguoiDungAdapter;
import com.example.bcedu.Model.KhoaHoc;
import com.example.bcedu.Model.NguoiDung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChiTietKhoaHoc extends AppCompatActivity {
    private ListView listViewDanhSachHocVien;
    private List<NguoiDung> mangNguoiDung;
    private List<KhoaHoc> mangKhoaHoc;
    private ImageButton back;
    private NguoiDungAdapter nguoiDungAdapter;
    private Button thamGiaBTN_CTKH;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiHocTap apiHocTap;

    private TextView tenKhoaHoc, moTaKhoaHoc, tenGiangVien;
    private ShapeableImageView hinhAnh;
    private KhoaHoc khoaHoc;
    private int maGiangVien;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        anhXa();

        // Kiểm tra dữ liệu truyền vào
        khoaHoc = (KhoaHoc) getIntent().getSerializableExtra("khoaHocDuocChon");

        if (isConnected(this)){
            getDuLieuKhoaHoc(khoaHoc.getMakhoahoc());
            getDanhSachThamGia();
            getGiangVien();
        } else {
            StyleableToast.makeText(this, "Không có Internet!", R.style.error_toast).show();
        }

        thamGiaBTN_CTKH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thamGiaBTN_CTKH.getText().toString().equalsIgnoreCase("Trang chủ")){
                    Intent intent = new Intent(getApplicationContext(), TrangChuKhoaHoc.class);
                    intent.putExtra("khoaHocCT", khoaHoc);
                    startActivity(intent);
                    finish();
                } else {
                    // Lấy ngày hôm nay
                    Date today = new Date();

                    // Định dạng ngày theo định dạng "dd/MM/yyyy"
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(today);

                    compositeDisposable.add(apiHocTap.thamGiaKhoaHocAPI(khoaHoc.getMakhoahoc(), Utils.nguoiDungCurrent.getManguoidung(), formattedDate)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    thamGiamodel -> {
                                        if (thamGiamodel.isSuccess() ) {
                                            StyleableToast.makeText(getApplicationContext(), thamGiamodel.getMessage() , R.style.success_toast).show();
                                            Intent intent = new Intent(getApplicationContext(), TrangChuKhoaHoc.class);

                                            intent.putExtra("khoaHocCT", khoaHoc);
                                            startActivity(intent);
                                        } else {
                                            StyleableToast.makeText(getApplicationContext(), thamGiamodel.getMessage() , R.style.error_toast).show();
                                        }
                                    },
                                    throwable -> {
                                        Log.e("CTKH", "CTKH: " + throwable.getMessage());
                                    }
                            ));
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

    private void getDuLieuKhoaHoc(int i) {
        tenKhoaHoc.setText(khoaHoc.getTenkhoahoc());
        moTaKhoaHoc.setText(khoaHoc.getGhichukhoahoc());
        Glide.with(this).load(Utils.BASE_URL +"images/"+ khoaHoc.getHinhanhmota()).into(hinhAnh);
    }

    private void getGiangVien(){
        compositeDisposable.add(apiHocTap.getGiangVien_API(khoaHoc.getMakhoahoc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model -> {
                            if (model.isSuccess() ) {
                                NguoiDung giangVien = model.getResult().get(0);
                                tenGiangVien.setText("Gv: "+giangVien.getTennguoidung());

                                if (Utils.nguoiDungCurrent.getManguoidung() == giangVien.getManguoidung()){
                                    thamGiaBTN_CTKH.setText("Trang chủ");
                                }
                            }
                        },
                        throwable -> {
                            Log.e("CTKH", "CTKH: " + throwable.getMessage());
                        }
                ));

    }

    private void getDanhSachThamGia() {
        compositeDisposable.add(apiHocTap.getDanhSachThamGiaKhoaHocAPI(khoaHoc.getMakhoahoc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        danhSachThamGiaModel -> {
                            if (danhSachThamGiaModel.isSuccess()){
                                mangNguoiDung = danhSachThamGiaModel.getResult();
                                nguoiDungAdapter = new NguoiDungAdapter(mangNguoiDung, this);
                                listViewDanhSachHocVien.setAdapter(nguoiDungAdapter);

                                List<NguoiDung> danhSachThamGia = danhSachThamGiaModel.getResult();
                                boolean isCurrentUserInList = false;

                                // Kiểm tra xem mã người dùng hiện tại có trong danh sách tham gia không
                                for (NguoiDung nguoiDung : danhSachThamGia) {
                                    if (nguoiDung.getManguoidung() == Utils.nguoiDungCurrent.getManguoidung()) {
                                        isCurrentUserInList = true;
                                        break;
                                    }
                                }

                                if (isCurrentUserInList) {
                                    thamGiaBTN_CTKH.setText("Trang chủ");
                                }
                            } else {
                                Log.d("LoiLoadDuLieuThanhVien", danhSachThamGiaModel.getMessage());
                            }
                        },
                        throwable -> {
                            // Xử lý ngoại lệ ở đây
                            Log.e("Error", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                        }
                ));
    }

    private void anhXa() {
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        mangNguoiDung = new ArrayList<>();
        listViewDanhSachHocVien = findViewById(R.id.listView_HocVien_ThamGia_KhoaHoc);
        nguoiDungAdapter = new NguoiDungAdapter(mangNguoiDung, this);

        tenKhoaHoc = findViewById(R.id.tenKhoaHoc_ChiTiet);
        moTaKhoaHoc = findViewById(R.id.moTa_KhoaHoc_ChiTiet);
        tenGiangVien = findViewById(R.id.giangVienKhoaHoc_ChiTiet);
        thamGiaBTN_CTKH = findViewById(R.id.thamGiaBTN_CTKH);

        hinhAnh = findViewById(R.id.logoKhoaHoc);
        back = findViewById(R.id.back_CTKH);
    }
    boolean isConnected(Context context) {
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

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chi_tiet_khoa_hoc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDuLieuKhoaHoc(khoaHoc.getMakhoahoc());
        getDanhSachThamGia();
        getGiangVien();
    }
}