package com.example.bcedu.Activities.KiemTra;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class ThemBaiKiemTra extends AppCompatActivity {
    private EditText linkAnh, tenBai, thoiLuong;
    private ShapeableImageView hinh;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiHocTap apiHocTap;
    private Button themBtn;
    private ImageButton back;
    private String mediaPath;
    private int maKhoaHoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        anhXa();

        maKhoaHoc = getIntent().getIntExtra("maKhoaHoc45", 0);
        eventClick();
    }

    private void eventClick() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        hinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ThemBaiKiemTra.this)
                        .crop()
                        .compress(1204)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        themBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_tenBai = tenBai.getText().toString().trim();
                String str_linkAnh = linkAnh.getText().toString().trim();
                String str_thoiLuong = thoiLuong.getText().toString().trim();

                if (TextUtils.isEmpty(str_linkAnh)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa chọn được ảnh vừa ý sao! Vui lòng chọn 1 ảnh!", R.style.warning).show();
                } else if (TextUtils.isEmpty(str_tenBai)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập tên bài kiểm tra!", R.style.warning).show();
                    tenBai.requestFocus();
                } else if (TextUtils.isEmpty(str_thoiLuong)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập thời lượng bài kiểm tra!", R.style.warning).show();
                    thoiLuong.requestFocus();
                } else {
                    try {
                        int thoiLuongInt = Integer.parseInt(str_thoiLuong);

                        compositeDisposable.add(apiHocTap.themBaiKiemTraAPI(maKhoaHoc, str_tenBai, str_linkAnh, thoiLuongInt)
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
                    } catch (NumberFormatException e) {
                        StyleableToast.makeText(getApplicationContext(), "Thời lượng bài kiểm tra không hợp lệ! Vui lòng nhập lại.", R.style.warning).show();
                        thoiLuong.requestFocus();
                    }
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            StyleableToast.makeText(this, "Bạn đã huỷ bỏ việc chọn ảnh", R.style.warning).show();
        } else if (resultCode == RESULT_OK) {
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
                        linkAnh.setText(serverResponse.getName());
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

    private void anhXa() {
        linkAnh = findViewById(R.id.linkAvtBaiKiemTra_TBKT);
        tenBai = findViewById(R.id.tenBaiKiemTra_TBKT);
        thoiLuong = findViewById(R.id.thoiLuongBaiKiemTra_TBKT);
        hinh = findViewById(R.id.pickAnhbaiKT_TBKT);
        themBtn = findViewById(R.id.themBaiKiemtra_TBKT);
        back = findViewById(R.id.backBtn_TBKT);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_bai_kiem_tra);
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