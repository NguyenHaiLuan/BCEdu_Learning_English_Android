package com.example.bcedu.Activities.Admin;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import com.example.bcedu.Model.CapDoTuVung;
import com.example.bcedu.Model.MessageModel;
import com.example.bcedu.Model.TuVung;
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

public class Admin_SuaTuVung extends AppCompatActivity {
    private EditText linkAnh, ten, loai, ynghia, phatam, viduminhhoa, audio;
    private ShapeableImageView pickAnh;
    private ImageButton back;
    private Button sua;
    private String mediaPath;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiHocTap apiHocTap;
    private TuVung tuVung = new TuVung();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        anhxa();
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        getDuLieuTuVung();

        eventCLick();
    }

    private void eventCLick() {
        pickAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(Admin_SuaTuVung.this)
                        .crop()
                        .compress(1204)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int matuvung = tuVung.getMatuvung();

                String str_ten = ten.getText().toString().trim();
                String str_loai = loai.getText().toString().trim();
                String str_link = linkAnh.getText().toString().trim();
                String str_nghia = ynghia.getText().toString().trim();
                String str_phatam = phatam.getText().toString().trim();
                String str_vidu = viduminhhoa.getText().toString().trim();
                String str_audio = audio.getText().toString().trim();

                if (TextUtils.isEmpty(str_ten)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập từ vựng!", R.style.warning).show();
                } else if (TextUtils.isEmpty(str_link)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa chọn ảnh! Nhấn vào hình để chọn!", R.style.warning).show();
                    ten.requestFocus();
                } else if (TextUtils.isEmpty(str_loai)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập từ loại!", R.style.warning).show();
                    loai.requestFocus();
                } else if (TextUtils.isEmpty(str_nghia)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập nghĩa!", R.style.warning).show();
                    ynghia.requestFocus();
                } else if (TextUtils.isEmpty(str_phatam)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa nhập phát âm!", R.style.warning).show();
                    phatam.requestFocus();
                } else if (TextUtils.isEmpty(str_vidu)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa thêm ví dụ!", R.style.warning).show();
                    viduminhhoa.requestFocus();
                } else if (TextUtils.isEmpty(str_audio)) {
                    StyleableToast.makeText(getApplicationContext(), "Bạn chưa thêm link audio!", R.style.warning).show();
                    viduminhhoa.requestFocus();
                } else {

                    compositeDisposable.add(apiHocTap.suaTuVung(matuvung, str_ten, str_loai, str_nghia, str_phatam, str_vidu, str_link, str_audio)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    capDoTuVungModel -> {
                                        if (capDoTuVungModel.isSuccess()) {
                                            StyleableToast.makeText(getApplicationContext(), capDoTuVungModel.getMessage(), R.style.success_toast).show();
                                            finish();
                                        } else {
                                            StyleableToast.makeText(getApplicationContext(), capDoTuVungModel.getMessage(), R.style.error_toast).show();
                                        }
                                    },
                                    throwable -> {
                                        // Xử lý ngoại lệ ở đây
                                        Log.e("logCDTV", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                                    }
                            ));
                }
            }
        });
    }


    private void getDuLieuTuVung() {
        tuVung = (TuVung) getIntent().getSerializableExtra("adapter_tuvung_admin_tuvung");

        Glide.with(getApplicationContext()).load(Utils.BASE_URL + "images/" + tuVung.getHinhanhmota()).into(pickAnh);

        linkAnh.setText(tuVung.getHinhanhmota().toString().trim());
        ten.setText(tuVung.getTentuvung().toString().trim());
        loai.setText(tuVung.getLoaituvung().toString().trim());
        ynghia.setText(tuVung.getYnghia().toString().trim());
        phatam.setText(tuVung.getPhatam().toString().trim());
        viduminhhoa.setText(tuVung.getViduminhhoa().toString().trim());
        audio.setText(tuVung.getAudio().toString().trim());
    }

    private void anhxa() {
        linkAnh = findViewById(R.id.linkanh_admin_sua);
        ten = findViewById(R.id.tentuvung_admin_sua);
        loai = findViewById(R.id.loaituvung_admin_sua);
        ynghia = findViewById(R.id.nghia_admin_sua);
        phatam = findViewById(R.id.phatam_admin_sua);
        viduminhhoa = findViewById(R.id.viduminhhoa_admin_sua);
        audio = findViewById(R.id.audio_admin_sua);

        back = findViewById(R.id.backBtn_suaTV_ADMIN);
        pickAnh = findViewById(R.id.pickAnhtuvung_suaTV_ADMIN);

        sua = findViewById(R.id.suaTuVung_admin_sua);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_sua_tu_vung);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}