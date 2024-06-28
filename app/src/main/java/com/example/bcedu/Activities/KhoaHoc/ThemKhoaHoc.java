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

public class ThemKhoaHoc extends AppCompatActivity {
    private EditText linkanh, ghiChu, tenKh;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiHocTap apiHocTap;
    private ImageButton back;
    private Button them;
    private ShapeableImageView pickImage;
    private String mediaPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        anhXa();

        linkanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ThemKhoaHoc.this)
                        .crop()
                        .compress(1204)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        them.setOnClickListener(new View.OnClickListener() {
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
                    compositeDisposable.add(apiHocTap.themKhoaHoc_API(Utils.nguoiDungCurrent.getManguoidung(), str_ten, str_ghiChu, str_linkAnh)
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

        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ThemKhoaHoc.this)
                        .crop()
                        .compress(1204)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });
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
        linkanh = findViewById(R.id.linkAvtKhoaHoc_TKH);
        ghiChu = findViewById(R.id.ghiChuKhoaHoc_TKH);
        tenKh = findViewById(R.id.tenKhoaHoc_TKH);
        back = findViewById(R.id.backBtn_Them_KH);
        them = findViewById(R.id.themKhoaHoc_TKH);
        pickImage = findViewById(R.id.pickAnhKhoaHoc_TKH);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_khoa_hoc);
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