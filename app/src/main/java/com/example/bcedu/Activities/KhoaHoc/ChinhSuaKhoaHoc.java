package com.example.bcedu.Activities.KhoaHoc;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.bcedu.Model.KhoaHoc;
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

public class ChinhSuaKhoaHoc extends AppCompatActivity {
    private EditText linkanh, ghiChu, tenKh;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiHocTap apiHocTap;
    private ImageButton back;
    private Button xong;
    private ShapeableImageView pickImage;
    private String mediaPath;
    private KhoaHoc khoaHoc = new KhoaHoc();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        anhXa();

        khoaHoc = (KhoaHoc) getIntent().getSerializableExtra("chitietKhoahocAdapter");
        getDuLieuKhoaHoc();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ChinhSuaKhoaHoc.this)
                        .crop()
                        .compress(1204)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        xong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_linkAnh = linkanh.getText().toString().trim();
                String str_ten = tenKh.getText().toString().trim();
                String str_ghiChu = ghiChu.getText().toString().trim();

                if (str_linkAnh.isEmpty()) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa chọn được ảnh đẹp sao? Hãy thử chọn lại!", R.style.warning).show();
                } else if (str_ten.isEmpty()) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập tên khoá học ạ!", R.style.warning).show();
                    tenKh.requestFocus();
                } else if (str_ghiChu.isEmpty()) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập ghi chú khoá học!", R.style.warning).show();
                    ghiChu.requestFocus();
                } else {
                    compositeDisposable.add(apiHocTap.suaKhoaHocAPI(str_ten, str_ghiChu, str_linkAnh, khoaHoc.getMakhoahoc())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    suaKHModel -> {
                                        if (suaKHModel.isSuccess()) {
                                            StyleableToast.makeText(getApplicationContext(), suaKHModel.getMessage(), R.style.success_toast).show();
                                            finish();
                                        } else {
                                            StyleableToast.makeText(getApplicationContext(), suaKHModel.getMessage(), R.style.error_toast).show();
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

    private void getDuLieuKhoaHoc() {
        linkanh.setText(khoaHoc.getHinhanhmota());
        tenKh.setText(khoaHoc.getTenkhoahoc());
        ghiChu.setText(khoaHoc.getGhichukhoahoc());

        Glide.with(getApplicationContext()).load(khoaHoc.getHinhanhmota()).into(pickImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            StyleableToast.makeText(this, "Bạn đã huỷ bỏ việc chọn ảnh", R.style.warning).show();
        } else {
            mediaPath = data.getDataString();
            uploadMultipleFiles();
            Log.d("Log", "OAR#:" + mediaPath);
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
                        linkanh.setText(serverResponse.getName());

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
        linkanh = findViewById(R.id.linkAvtKhoaHoc_CSKH);
        ghiChu = findViewById(R.id.ghiChuKhoaHoc_CSKH);
        tenKh = findViewById(R.id.tenKhoaHoc_CSKH);
        back = findViewById(R.id.backBtn_CSKH);
        xong = findViewById(R.id.hoanThanhBTN_CSKH);
        pickImage = findViewById(R.id.pickAnhKhoaHoc_CSKH);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chinh_sua_khoa_hoc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}