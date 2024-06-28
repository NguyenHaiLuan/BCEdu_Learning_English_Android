package com.example.bcedu.Activities.NguoiDung;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bcedu.Activities.MainActivity;
import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.Dialog.DialogNoInternet;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.io.File;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignIn extends AppCompatActivity {
    private Button signInBtn, quenMatKhauBtn;
    private ImageButton hienAnMatKhau, dangNhapGoogle;
    private TextView chuyenDangKi;
    private EditText email, pass;
    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private DialogAppLoading dialogAppLoading;
    private Boolean doubleBack = false;
    boolean passwordVisible = false; // Ban đầu ẩn mật khẩu
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        anhXa();
        kiemTraKetNoiInterNet();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set onClick listener for Google Sign-In button
        findViewById(R.id.dangNhapGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        ButtonEventListenner();
    }


    private void ButtonEventListenner() {
        // Nút đăng nhập
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_email = email.getText().toString().trim();
                String str_pass = pass.getText().toString().trim();

                if (TextUtils.isEmpty(str_email)) {
                    StyleableToast.makeText(SignIn.this, "Bạn chưa nhập email!", R.style.warning).show();
                    email.requestFocus();
                } else if (TextUtils.isEmpty(str_pass)) {
                    StyleableToast.makeText(SignIn.this, "Bạn chưa nhập mật khẩu!", R.style.warning).show();
                    pass.requestFocus();
                } else {
                    Paper.book().write("email", str_email);
                    Paper.book().write("pass", str_pass);
                    showLoadingAnimation();
                    compositeDisposable.add(apiHocTap.dangNhapAPI(str_email, str_pass)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    dangNhapHocVienModel -> {
                                        if (dangNhapHocVienModel.isSuccess()) {
                                            hideLoadingAnimation();
                                            Utils.nguoiDungCurrent = dangNhapHocVienModel.getResult().get(0);
                                            getBacRank();
                                            getBacRankTiepTheo();
                                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                                            StyleableToast.makeText(SignIn.this, "Xin chào " + Utils.nguoiDungCurrent.getTennguoidung().trim(), R.style.thongBaoDiem).show();
                                            startActivity(intent);
                                        } else {
                                            hideLoadingAnimation();
                                            StyleableToast.makeText(getApplicationContext(), dangNhapHocVienModel.getMessage(), Toast.LENGTH_LONG, R.style.error_toast).show();
                                            Log.d("LoiDangNhap", dangNhapHocVienModel.getMessage());
                                        }
                                    }, throwable -> {
                                        hideLoadingAnimation();
                                        StyleableToast.makeText(SignIn.this, "Lỗi: " + throwable.getMessage(), R.style.error_toast).show();
                                        Log.d("LoiDangNhap_Server", throwable.getMessage());
                                    }
                            ));
                }
            }
        });
        //nút đăng kí
        chuyenDangKi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        //Nút quên mat khẩu
        quenMatKhauBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, QuenMatKhau.class);
                startActivity(intent);
            }
        });

        //Nút hiện và ẩn mật khẩu
        hienAnMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVisible = !passwordVisible; // Đảo ngược trạng thái hiện tại
                if (passwordVisible) {
                    // Hiển thị mật khẩu
                    pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    hienAnMatKhau.setImageResource(R.drawable.baseline_blackbulb_24);
                } else {
                    // Ẩn mật khẩu
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    hienAnMatKhau.setImageResource(R.drawable.baseline_lightbulb_24);
                }
                // Di chuyển con trỏ về cuối EditText
                pass.setSelection(pass.getText().length());
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    checkIfUserExists(account);
                }
            } catch (ApiException e) {
                Log.w("Google Sign In", "Google sign in failed", e);
            }
        }
    }

    private void checkIfUserExists(GoogleSignInAccount account) {
        String email = account.getEmail();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            List<String> signInMethods = task.getResult().getSignInMethods();
                            if (signInMethods != null && signInMethods.contains(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)) {
                                // Tài khoản đã tồn tại, thực hiện đăng nhập
                                signIn(account);
                            } else {
                                // Tài khoản không tồn tại, thực hiện đăng ký
                                signUp(account);
                            }
                        } else {
                            Log.e("FirebaseAuth", "Error fetching sign-in methods for email", task.getException());
                        }
                    }
                });
    }

    private void signIn(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {

                                String displayName = user.getDisplayName();
                                String email = user.getEmail();
                                String uid = user.getUid();
                                String pass = uid.substring(0, Math.min(uid.length(), 10));

                                handleSignInSuccess(email, pass);
                            }
                        } else {
                            Log.w("Google Sign In", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void signUp(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String displayName = user.getDisplayName();
                                String email = user.getEmail();
                                String uid = user.getUid();
                                String pass = uid.substring(0, Math.min(uid.length(), 10));
                                String sdt = user.getPhoneNumber();

                                handleSignUpSuccess(email, displayName, pass, sdt);
                            }
                        } else {
                            Log.w("Google Sign In", "signUpWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void handleSignUpSuccess(String str_email, String str_name, String str_pass, String sdt) {
        compositeDisposable.add(apiHocTap.dangKiNguoiDungAPI(str_email, str_name, str_pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dangKiModel -> {
                            if (dangKiModel.isSuccess()) {
                                StyleableToast.makeText(this, "Bạn đã là 1 người dùng mới!", R.style.success_toast).show();
                                Utils.nguoiDungCurrent.setEmail(str_email);
                                Utils.nguoiDungCurrent.setMatkhau(str_pass);
                                Intent intent = new Intent(getApplicationContext(), ChiTietSignUp.class);
                                intent.putExtra("emailNguoiDungMoi", str_email);
                                intent.putExtra("matKhauNguoiDungMoi", str_pass);
                                intent.putExtra("tenNguoiDungMoi", str_name);
                                startActivity(intent);
                                finish();
                            } else {
                                StyleableToast.makeText(this, dangKiModel.getMessage(), R.style.success_toast).show();
                            }

                        }, throwable -> {
                            StyleableToast.makeText(this, "Chưa thể đăng kí! Lỗi: " + throwable.getMessage(), R.style.error_toast).show();
                            Log.e("Loi", "DangKi: " + throwable.getMessage());
                        }
                ));
    }

    private void handleSignInSuccess(String str_email, String str_pass) {
        compositeDisposable.add(apiHocTap.dangNhapAPI(str_email, str_pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dangNhapHocVienModel -> {
                            if (dangNhapHocVienModel.isSuccess()) {
                                Utils.nguoiDungCurrent = dangNhapHocVienModel.getResult().get(0);
                                getBacRank();
                                getBacRankTiepTheo();
                                Intent intent = new Intent(SignIn.this, MainActivity.class);
                                StyleableToast.makeText(SignIn.this, "Xin chào " + Utils.nguoiDungCurrent.getTennguoidung().trim(), R.style.thongBaoDiem).show();
                                startActivity(intent);
                            } else {
                                StyleableToast.makeText(getApplicationContext(), dangNhapHocVienModel.getMessage(), Toast.LENGTH_LONG, R.style.error_toast).show();
                                Log.d("LoiDangNhap", dangNhapHocVienModel.getMessage());
                            }
                        }, throwable -> {
                            hideLoadingAnimation();
                            StyleableToast.makeText(SignIn.this, "Lỗi: " + throwable.getMessage(), R.style.error_toast).show();
                            Log.d("LoiDangNhap_Server", throwable.getMessage());
                        }
                ));
    }

    private void kiemTraKetNoiInterNet() {
        if (isConnected(this)) {
        } else {
            // Nếu không có internet thì khởi chạy no internet Dialog
            DialogNoInternet noInternetConnection = new DialogNoInternet(SignIn.this);
            noInternetConnection.setCancelable(false);
            noInternetConnection.getWindow().setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)));
            noInternetConnection.show();
        }
    }

    private void getBacRank() {
        compositeDisposable.add(apiHocTap.getXepHangAPI(Utils.nguoiDungCurrent.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model2 -> {
                            if (model2.isSuccess()) {
                                Utils.xepHangCuaToi = model2.getResult().get(0);
                            } else {
                            }
                        }, throwable -> {
                            Log.e("Loi", "get_xephang: " + throwable.getMessage());
                        }
                ));
    }

    private void getBacRankTiepTheo() {
        compositeDisposable.add(apiHocTap.getXepHangMoiAPI(Utils.nguoiDungCurrent.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model3 -> {
                            if (model3.isSuccess()) {
                                Utils.xepHangTiepTheo = model3.getResult().get(0);
                            } else {
                            }
                        }, throwable -> {
                            Log.e("Loi", "get_xephang: " + throwable.getMessage());
                        }
                ));
    }

    private boolean isConnected(Context context) {
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

    private void anhXa() {
        Paper.init(this);

        dialogAppLoading = new DialogAppLoading(this);

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        signInBtn = findViewById(R.id.logInBtn);
        dangNhapGoogle = findViewById(R.id.dangNhapGoogle);
        chuyenDangKi = findViewById(R.id.chuyenDangKiBtn);
        quenMatKhauBtn = findViewById(R.id.quenMatKhauBtn);
        hienAnMatKhau = findViewById(R.id.hienAnMatKhau);

        email = findViewById(R.id.emailLogin);
        pass = findViewById(R.id.passwordLogin);

        // đọc dữ liệu từ paper
        if (Paper.book().read("email") != null && Paper.book().read("pass") != null) {
            email.setText(Paper.book().read("email"));
            pass.setText(Paper.book().read("pass"));
        }


    }

    private void showLoadingAnimation() {
        dialogAppLoading.show();
    }

    private void hideLoadingAnimation() {
        dialogAppLoading.cancel();
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.nguoiDungCurrent.getEmail() != null && Utils.nguoiDungCurrent.getMatkhau() != null) {
            email.setText(Utils.nguoiDungCurrent.getEmail());
            pass.setText(Utils.nguoiDungCurrent.getMatkhau());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (doubleBack) {
            super.onBackPressed();
            finish();
            //thoát ra Login
            moveTaskToBack(true);
        }

        this.doubleBack = true;
        StyleableToast.makeText(getApplicationContext(), "Nhấn lần nữa để thoát", R.style.warning).show();
        //thời gian chờ là 2s
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBack = false;
            }
        }, 2000);
    }
}