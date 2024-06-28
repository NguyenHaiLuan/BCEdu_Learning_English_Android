package com.example.bcedu.Activities.NguoiDung;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.Model.MessageModel;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChiTietSignUp extends AppCompatActivity {
    private String mediaPath;
    private Spinner gioiTinhSpinner;
    private ShapeableImageView avt;
    private EditText sdt, avtEdittext, ngay;
    private TextView  wellcomeText;
    private LinearLayout khoiEditHinh;
    private Spinner spinnerRole;
    private Button confirmBtn;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiHocTap apiHocTap;
    private DialogAppLoading dialogAppLoading;
    private ImageView pickDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        initGiaoDien();
        anhXa();

        getDuLieuNguoiDungMoi();
        setUpDuLieuSpinner_GioiTinh();
        setUpDuLieuSpinner_Vaitro();

        clickEvent();
    }

    private void openDialog() {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String nam = String.valueOf(year);
                String thang = String.valueOf(month+1);
                String ngaya = String.valueOf(dayOfMonth);

                // 6/9/2002
                ngay.setText(ngaya + "/" + thang + "/" + nam);
            }
        }, 2002, 9, 6);

        dialog.show();
    }

    private void handleConfirmClick() {
        String gioiTinh = gioiTinhSpinner.getSelectedItem().toString().trim();
        String str_vaitro = spinnerRole.getSelectedItem().toString().trim();

        String ten = Utils.nguoiDungDK.getTennguoidung();
        int maNguoiDung = Utils.nguoiDungDK.getManguoidung();
        String email = Utils.nguoiDungDK.getEmail();
        String str_avt = avtEdittext.getText().toString().trim();
        String str_sdt = sdt.getText().toString().trim();
        String date = ngay.getText().toString().trim();

        if (TextUtils.isEmpty(str_avt)) {
            StyleableToast.makeText(getApplicationContext(), "Bạn chưa chọn được ảnh vừa ý sao! Vui lòng chọn 1 ảnh!", R.style.warning).show();
            avtEdittext.requestFocus();
        }  else if (TextUtils.isEmpty(str_sdt)) {
            StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập số điện thoại!", R.style.warning).show();
            ngay.requestFocus();
        } else if (TextUtils.isEmpty(date)) {
            StyleableToast.makeText(getApplicationContext(), "Bạn chưa chọn ngày sinh ạ!", R.style.warning).show();
            sdt.requestFocus();
        } else {
            compositeDisposable.add(apiHocTap.updateThongTinNguoiDungAPI(ten, email, str_sdt, str_avt, gioiTinh, date, str_vaitro, maNguoiDung)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            updateNDModel -> {
                                if (updateNDModel.isSuccess()) {
                                    StyleableToast.makeText(ChiTietSignUp.this, updateNDModel.getMessage(), R.style.success_toast).show();
                                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    StyleableToast.makeText(ChiTietSignUp.this, updateNDModel.getMessage(), R.style.error_toast).show();
                                }
                            },
                            throwable -> {
                                Log.e("A1", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                            }
                    ));
        }
    }

    private void getDuLieuNguoiDungMoi() {
        String emailNguoiDung = getIntent().getStringExtra("emailNguoiDungMoi");
        String passNguoiDung = getIntent().getStringExtra("matKhauNguoiDungMoi");
        compositeDisposable.add(apiHocTap.dangNhapAPI(emailNguoiDung, passNguoiDung)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dangNhapHocVienModel -> {
                            if (dangNhapHocVienModel.isSuccess()) {
                                Utils.nguoiDungDK = dangNhapHocVienModel.getResult().get(0);
                            } else {
                                Log.d("LoiCTDK", dangNhapHocVienModel.getMessage());
                            }
                        }, throwable -> {
                            Log.d("LoiCTDK_Server", throwable.getMessage());
                        }
                ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            StyleableToast.makeText(this, "Bạn đã huỷ bỏ việc chọn ảnh", R.style.warning).show();
        } else if (resultCode == RESULT_OK){
            mediaPath = data.getDataString();
            uploadMultipleFiles();
        }
    }


    // Uploading Image/Video
    private void uploadMultipleFiles() {
        Uri uri = Uri.parse(mediaPath);

        File file = new File(getPath(uri));
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);

        MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file", file.getName(), requestBody1);

        Call<MessageModel> call = apiHocTap.uploadFile(fileToUpload1);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                MessageModel serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.isSuccess()) {
                        StyleableToast.makeText(getApplicationContext(), serverResponse.getMessage(), R.style.success_toast).show();
                        avtEdittext.setText(serverResponse.getName());
                    } else {
                        StyleableToast.makeText(getApplicationContext(), serverResponse.getMessage(), R.style.error_toast).show();
                    }
                } else {
                    StyleableToast.makeText(getApplicationContext(), serverResponse.getMessage(), R.style.error_toast).show();
                    Log.v("Response", serverResponse.toString());
                }
            }

            @Override
            public void onFailure(Call<MessageModel> call, Throwable t) {
                StyleableToast.makeText(getApplicationContext(), t.getMessage(), R.style.error_toast).show();
                Log.d("log", t.getMessage());
            }
        });
    }

    private String getPath(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    private void setUpDuLieuSpinner_GioiTinh() {
        String[] gioiTinh = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> gioiTinhAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gioiTinh);
        gioiTinhAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gioiTinhSpinner.setAdapter(gioiTinhAdapter);
    }

    private void setUpDuLieuSpinner_Vaitro() {
        String[] vaitro = {"Học viên", "Giảng viên"};
        ArrayAdapter<String> roleapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vaitro);
        roleapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleapt);
    }


    private void clickEvent() {
        avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ChiTietSignUp.this)
                        .crop()
                        .compress(1204)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleConfirmClick();
            }
        });

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

    }

    private void showLoadingAnimation() {
        dialogAppLoading.show();
    }

    private void hideLoadingAnimation() {
        dialogAppLoading.cancel();
    }


    private void anhXa() {
        sdt = findViewById(R.id.sdtCTDK);
        avt = findViewById(R.id.avtCTDK);
        confirmBtn = findViewById(R.id.confirmBtnCTDK);

        gioiTinhSpinner = findViewById(R.id.gioiTinhSpinnerCTDK);
        spinnerRole = findViewById(R.id.vaiTroSpinner);

        khoiEditHinh = findViewById(R.id.khoiEditPhotoCTDK);
        avtEdittext = findViewById(R.id.avtCTDKtext);

        ngay = findViewById(R.id.ngaySinhCTDK);
        pickDate = findViewById(R.id.pickDateCTDK);
        // Text để chào mừng và hiển thị tên của người dùng
        wellcomeText = findViewById(R.id.wellcomeTextCTDK);

        // Hiển thị tên người dùng mới
        String tenNguoiDungMoi = getIntent().getStringExtra("tenNguoiDungMoi");
        wellcomeText.setText("Chào mừng " + tenNguoiDungMoi);

        dialogAppLoading = new DialogAppLoading(this);

    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chi_tiet_sign_up);
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