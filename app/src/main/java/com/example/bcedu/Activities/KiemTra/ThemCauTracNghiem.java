package com.example.bcedu.Activities.KiemTra;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bcedu.Model.BaiKiemTra;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThemCauTracNghiem extends AppCompatActivity {
    private EditText cauhoi, a, b, c, d;
    private Spinner dapAnDungSpinner;
    private Button them;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiHocTap apiHocTap;
    private BaiKiemTra baiKiemTra = new BaiKiemTra();
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        anhXa();

        baiKiemTra = (BaiKiemTra) getIntent().getSerializableExtra("BaiKiemTra_QL");
        setDuLieuSpinner_DapAnDung();
        eventClick();
    }

    private void eventClick() {
        them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_cauhoi = cauhoi.getText().toString().trim();
                String str_a = a.getText().toString().trim();
                String str_b = b.getText().toString().trim();
                String str_c = c.getText().toString().trim();
                String str_d = d.getText().toString().trim();
                String str_dapAndung = dapAnDungSpinner.getSelectedItem().toString().trim();

                if (TextUtils.isEmpty(str_cauhoi)){
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập câu hỏi!", R.style.warning).show();
                    cauhoi.requestFocus();
                } else if (TextUtils.isEmpty(str_a)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập đáp án A!", R.style.warning).show();
                    a.requestFocus();
                } else if (TextUtils.isEmpty(str_b)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập đáp án B!", R.style.warning).show();
                    b.requestFocus();
                }else if (TextUtils.isEmpty(str_c)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập đáp án C!", R.style.warning).show();
                    c.requestFocus();
                }else if (TextUtils.isEmpty(str_d)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập đáp án D!", R.style.warning).show();
                    d.requestFocus();
                } else {
                    int maBaiKiemTra = baiKiemTra.getMabaikiemtra();
                    compositeDisposable.add(apiHocTap.themCauTracNghiemAPI(maBaiKiemTra, str_cauhoi, str_a, str_b, str_c, str_d, str_dapAndung)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    themKHModel -> {
                                        if (themKHModel.isSuccess()) {
                                            StyleableToast.makeText(getApplicationContext(), themKHModel.getMessage(), R.style.success_toast).show();
                                            finish();
                                        } else {
                                            StyleableToast.makeText(getApplicationContext(), themKHModel.getMessage(), R.style.error_toast).show();
                                        }
                                    },
                                    throwable -> {
                                        // Xử lý ngoại lệ ở đây
                                        Log.e("Error", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
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

    private void setDuLieuSpinner_DapAnDung() {
        String[] gioiTinh = {"A", "B","C","D"};
        ArrayAdapter<String> gioiTinhAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gioiTinh);
        gioiTinhAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dapAnDungSpinner.setAdapter(gioiTinhAdapter);
    }

    private void anhXa() {
        cauhoi = findViewById(R.id.cauHoi_TTN);
        a = findViewById(R.id.dapAnA_TTN);
        b = findViewById(R.id.dapAnB_TTN);
        c = findViewById(R.id.dapAnC_TTN);
        d = findViewById(R.id.dapAnD_TTN);
        dapAnDungSpinner = findViewById(R.id.dapAnDung_Spinner_TTN);
        them = findViewById(R.id.themCauTracNghiem);
        back = findViewById(R.id.backBtn_TTN);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_cau_trac_nghiem);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}