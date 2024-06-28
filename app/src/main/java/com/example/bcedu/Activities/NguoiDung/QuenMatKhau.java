package com.example.bcedu.Activities.NguoiDung;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QuenMatKhau extends AppCompatActivity {
    private Button layLaiMatKhauBtn;
    private EditText emailEdt;
    private DialogAppLoading dialogAppLoading;

    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        anhXa();
        buttonClickEvent();
    }

    private void buttonClickEvent() {
        layLaiMatKhauBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAppLoading.show();
                String email = emailEdt.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    dialogAppLoading.cancel();
                    StyleableToast.makeText(QuenMatKhau.this, "Bạn chưa nhập email!", R.style.warning).show();
                    emailEdt.requestFocus();
                } else {
                    compositeDisposable.add(apiHocTap.resetMatKhauAPI(email)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    resetModel -> {
                                        if (resetModel.isSuccess()){
                                            dialogAppLoading.cancel();
                                            StyleableToast.makeText(getApplicationContext(), "Đã gửi link tới email của bạn! Nó có thể nằm ở mục thư rác!",Toast.LENGTH_LONG, R.style.success_toast).show();
                                            Intent intent = new Intent(QuenMatKhau.this, SignIn.class);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            dialogAppLoading.cancel();
                                            StyleableToast.makeText(QuenMatKhau.this, resetModel.getMessage(), R.style.error_toast).show();
                                        }
                                    }, throwable ->{
                                        dialogAppLoading.cancel();
                                        StyleableToast.makeText(QuenMatKhau.this, "Chưa thể gửi email reset! Lỗi: " + throwable.getMessage(), R.style.error_toast).show();
                                        Log.e("Loi", "QuenMatKhau: " + throwable.getMessage());
                                    }
                            ));
                }
            }
        });
    }

    private void anhXa() {
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        layLaiMatKhauBtn = findViewById(R.id.layLaiMatKhauBtn);
        emailEdt = findViewById(R.id.emailQuenMatKhau);

        dialogAppLoading = new DialogAppLoading(this);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quen_mat_khau);
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