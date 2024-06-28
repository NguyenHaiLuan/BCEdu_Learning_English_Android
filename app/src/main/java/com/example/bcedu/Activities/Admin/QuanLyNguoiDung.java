package com.example.bcedu.Activities.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bcedu.Adapter.AdapterAllNguoiDung;
import com.example.bcedu.Adapter.KhoaHocTheoGiangVienAdapter;
import com.example.bcedu.Model.NguoiDung;
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

public class QuanLyNguoiDung extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdapterAllNguoiDung adapter;
    private List<NguoiDung> mangNguoiDung;
    private ImageButton back;
    private SearchView searchView;
    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        anhXa();
        getTatCaNguoiDung();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Thiết lập cho SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText); // Gọi phương thức filter của adapter khi text trong SearchView thay đổi
                return false;
            }
        });
    }

    private void getTatCaNguoiDung() {
        compositeDisposable.add(apiHocTap.getAllNguoiDungAPI()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        nguoiDungModel -> {
                            if (nguoiDungModel.isSuccess()){
                                mangNguoiDung = nguoiDungModel.getResult();
                                adapter = new AdapterAllNguoiDung(mangNguoiDung, this);
                                recyclerView.setAdapter(adapter);
                                // Start layout animation
                                recyclerView.scheduleLayoutAnimation();

                            }
                        }, throwable -> {
                            StyleableToast.makeText(this, "Không kết nối được với server!", R.style.error_toast).show();
                            Log.e("logTVTCD", "AAA: " + throwable.getMessage());
                        }
                ));
    }

    private void anhXa() {
        recyclerView = findViewById(R.id.recyclerView_allNguoiDung);
        mangNguoiDung = new ArrayList<>();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        back =findViewById(R.id.back_QLND_admin);
        searchView =findViewById(R.id.search_nguoidung_qlnd_admin);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quan_ly_nguoi_dung);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}