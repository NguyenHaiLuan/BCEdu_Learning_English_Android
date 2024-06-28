package com.example.bcedu.Activities.KhoaHoc;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.bcedu.Adapter.KhoaHocAdapter;
import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.Model.KhoaHoc;
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

public class KhoaHocActivity extends AppCompatActivity {

    private ListView listViewKhoaHoc;
    private TextView textGioiThieu_KH;
    private List<com.example.bcedu.Model.KhoaHoc> mangKhoaHoc;
    private KhoaHocAdapter khoaHocAdapter;
    private ImageButton  back;
    private SearchView searchView;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiHocTap apiHocTap;
    private LottieAnimationView lottieAnimationView;
    private DialogAppLoading dialogAppLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();


        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        anhXa();

        dialogAppLoading.show();
        // Thêm animation nếu không có khoá học nào trong list
        initAnimation();



        // Xu li sự kiện nếu có kết nối internet
        if (Utils.isConnected(this)) {
            //load dữ liệu của các khoá học
            getKhoaHoc();
        } else {
            StyleableToast.makeText(this, "Không có Internet!", R.style.error_toast).show();
        }

        // Xử lí sự kiện khi chọn vào item của list view khoá học
        itemListViewClickListener();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void itemListViewClickListener() {
        listViewKhoaHoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy thông tin của khoá học từ danh sách
                KhoaHoc khoaHoc = mangKhoaHoc.get(position);
                int maKhoaHoc = khoaHoc.getMakhoahoc(); // Lấy mã khoá học từ đối tượng khoaHoc

                Intent intent = new Intent(getApplicationContext(), ChiTietKhoaHoc.class);
                intent.putExtra("khoaHocDuocChon", khoaHoc);
                startActivity(intent);
            }
        });
    }

    private void initAnimation() {
        // Đặt file json cho animation
        lottieAnimationView.setAnimation(R.raw.nodata);
        // Đặt chế độ lặp lại cho animation thành vô hạn
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        // Khởi chạy animation
        lottieAnimationView.playAnimation();

        if (Utils.nguoiDungCurrent.getVaitro().equalsIgnoreCase("Học viên")) {
            textGioiThieu_KH.setText("Chưa có thầy cô nào tạo khoá học bạn ơi! Hãy quay lại sau nhé!");
        } else {
            textGioiThieu_KH.setText("Hãy là người đầu tiên tạo khoá học! Nhanh! Khẩn trương!");
        }
    }

    private void getKhoaHoc() {
        compositeDisposable.add(apiHocTap.getKhoaHocAPI()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        khoaHocModel -> {
                            if (khoaHocModel.isSuccess()) {
                                mangKhoaHoc = khoaHocModel.getResult();
                                lottieAnimationView.setVisibility(View.INVISIBLE);
                                textGioiThieu_KH.setVisibility(View.INVISIBLE);
                                khoaHocAdapter = new KhoaHocAdapter(mangKhoaHoc, this);
                                listViewKhoaHoc.setAdapter(khoaHocAdapter);

                                dialogAppLoading.cancel();

                                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        khoaHocAdapter.filter(newText);
                                        return true;
                                    }
                                });
                            }
                            else {
                                dialogAppLoading.cancel();
                                StyleableToast.makeText(this, "Chưa có khoá học nào được tạo!", R.style.warning).show();
                            }

                        },
                        throwable -> {
                            // Xử lý ngoại lệ ở đây
                            Log.e("Error", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                        }
                ));
    }

    private void anhXa() {
        lottieAnimationView = findViewById(R.id.lottie_animation_view_nodata);
        textGioiThieu_KH = findViewById(R.id.textGioiThieu_KH);
        listViewKhoaHoc = findViewById(R.id.listview_khoahoc);
        mangKhoaHoc = new ArrayList<>();

        back = findViewById(R.id.back_KH);
        searchView = findViewById(R.id.search_khoaHoc);
        dialogAppLoading = new DialogAppLoading(this);
    }


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_khoa_hoc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getKhoaHoc();
    }
}