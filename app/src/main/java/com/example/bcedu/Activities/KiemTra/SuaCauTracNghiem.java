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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bcedu.Model.BaiKiemTra;
import com.example.bcedu.Model.CauTracNghiem;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SuaCauTracNghiem extends AppCompatActivity {
    private EditText cauhoi, a, b, c, d;
    private Spinner dapAnDungSpinner;
    private Button edit;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiHocTap apiHocTap;
    private BaiKiemTra baiKiemTra = new BaiKiemTra();
    private ImageButton back;
    private CauTracNghiem cauTracNghiem = new CauTracNghiem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        anhXa();

        // set dữ liệu cho spinner Đáp án đúng
        setDuLieuSpinner();

        cauTracNghiem = (CauTracNghiem) getIntent().getSerializableExtra("CauTracNghiem69");

        // get dữ liệu của câu trắc nghiệm
        getDuLieuCauTN();

        eventClickListenner();
    }

    private void eventClickListenner() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_cauhoi = cauhoi.getText().toString().trim();
                String str_a = a.getText().toString().trim();
                String str_b = b.getText().toString().trim();
                String str_c = c.getText().toString().trim();
                String str_d = d.getText().toString().trim();
                String str_dapAndung = dapAnDungSpinner.getSelectedItem().toString().trim();

                if (TextUtils.isEmpty(str_cauhoi)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập câu hỏi!", R.style.warning).show();
                    cauhoi.requestFocus();
                } else if (TextUtils.isEmpty(str_a)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập đáp án A!", R.style.warning).show();
                    a.requestFocus();
                } else if (TextUtils.isEmpty(str_b)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập đáp án B!", R.style.warning).show();
                    b.requestFocus();
                } else if (TextUtils.isEmpty(str_c)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập đáp án C!", R.style.warning).show();
                    c.requestFocus();
                } else if (TextUtils.isEmpty(str_d)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập đáp án D!", R.style.warning).show();
                    d.requestFocus();
                } else {
                    int maCauTN = cauTracNghiem.getMacautracnghiem();
                    compositeDisposable.add(apiHocTap.suaCauTracNghiemAPI(str_cauhoi, str_a, str_b, str_c, str_d, str_dapAndung, maCauTN)
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
    }


    private void getDuLieuCauTN() {
        cauhoi.setText(cauTracNghiem.getCauhoi().toString().trim());
        a.setText(cauTracNghiem.getDapana().toString().trim());
        b.setText(cauTracNghiem.getDapanb().toString().trim());
        c.setText(cauTracNghiem.getDapanc().toString().trim());
        d.setText(cauTracNghiem.getDapand().toString().trim());

        String str_dapAnDung = cauTracNghiem.getDapandung();
        // Tìm vị trí của giới tính trong mảng Thể loại
        int loaiIndex = -1;
        String[] gioiTinhArray = {"A", "B", "C", "D"};
        for (int i = 0; i < gioiTinhArray.length; i++) {
            if (gioiTinhArray[i].equals(str_dapAnDung)) {
                loaiIndex = i;
                break;
            }
        }
        // Đặt giá trị cho Spinner giới tính
        if (loaiIndex != -1) {
            dapAnDungSpinner.setSelection(loaiIndex);
        }
    }

    private void setDuLieuSpinner() {
        String[] gioiTinh = {"A", "B", "C", "D"};
        ArrayAdapter<String> gioiTinhAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gioiTinh);
        gioiTinhAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dapAnDungSpinner.setAdapter(gioiTinhAdapter);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sua_cau_trac_nghiem);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void anhXa() {
        cauhoi = findViewById(R.id.cauHoi_CSTN);
        a = findViewById(R.id.dapAnA_CSTN);
        b = findViewById(R.id.dapAnB_CSTN);
        c = findViewById(R.id.dapAnC_CSTN);
        d = findViewById(R.id.dapAnD_CSTN);
        dapAnDungSpinner = findViewById(R.id.dapAnDung_Spinner_CSTN);
        edit = findViewById(R.id.edit_CSTN);
        back = findViewById(R.id.backBtn_CSTN);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}