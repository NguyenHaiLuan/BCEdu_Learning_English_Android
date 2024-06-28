package com.example.bcedu.Activities.NguoiDung;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignUp extends AppCompatActivity {
    private EditText email, pass, repass, name, usernameLogin;
    private Button signUp;



    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        anhXa();
        signUpEvent();
    }

    private void signUpEvent() {
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangKi();
            }
        });
    }

    private void dangKi() {
        String str_email = email.getText().toString().trim();
        String str_name = name.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();
        String str_rePass = repass.getText().toString().trim();

        if (TextUtils.isEmpty(str_email)){
            StyleableToast.makeText(this, "Bạn chưa nhập email!", R.style.warning).show();
            email.requestFocus();
        } else if (TextUtils.isEmpty(str_name)){
            StyleableToast.makeText(this, "Bạn chưa nhập tên!", R.style.warning).show();
            name.requestFocus();
        } else if (TextUtils.isEmpty(str_pass)){
            StyleableToast.makeText(this, "Bạn chưa nhập mật khẩu!", R.style.warning).show();
            pass.requestFocus();
        }else if (TextUtils.isEmpty(str_rePass)){
            StyleableToast.makeText(this, "Bạn chưa nhập lại mật khẩu", R.style.warning).show();
            repass.requestFocus();
        } else {
            if (str_pass.equals(str_rePass)) {
                compositeDisposable.add(apiHocTap.dangKiNguoiDungAPI(str_email, str_name, str_pass)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                dangKiModel -> {
                                    if (dangKiModel.isSuccess()){
                                        StyleableToast.makeText(this, "Bạn đã là 1 người dùng mới!", R.style.success_toast).show();
                                        Utils.nguoiDungCurrent.setEmail(str_email);
                                        Utils.nguoiDungCurrent.setMatkhau(str_pass);
                                        Intent intent = new Intent(SignUp.this, ChiTietSignUp.class);

                                        intent.putExtra("emailNguoiDungMoi", str_email);
                                        intent.putExtra("matKhauNguoiDungMoi", str_pass);
                                        intent.putExtra("tenNguoiDungMoi", str_name);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        StyleableToast.makeText(this, dangKiModel.getMessage(), R.style.success_toast).show();
                                    }

                                }, throwable ->{
                                    StyleableToast.makeText(this, "Chưa thể đăng kí! Lỗi: " + throwable.getMessage(), R.style.error_toast).show();
                                    Log.e("Loi", "DangKi: " + throwable.getMessage());
                                }
                        ));
            } else {
                StyleableToast.makeText(this, "Bạn nhập lại mật khẩu chưa khớp", R.style.warning).show();
                repass.requestFocus();
            }
        }
    }

    private void anhXa() {
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        email = findViewById(R.id.emailSignUp);
        pass = findViewById(R.id.passwordSignUp);
        repass = findViewById(R.id.passwordSignUpAgain);
        name = findViewById(R.id.nameEdt);
        usernameLogin = findViewById(R.id.emailLogin);

        signUp = findViewById(R.id.signUpBtn);



    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
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