package com.example.bcedu.Activities.NguoiDung;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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

import com.bumptech.glide.Glide;
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

public class ChinhSuaThongTinCaNhan extends AppCompatActivity {
    private EditText ten, email, sdt, avtEdit, ngay;
    private TextView editText;
    private Button editBtn;
    private ImageButton backBtn;
    private ImageView pickDate;
    private LinearLayout khoiEditHinh;
    private Spinner gioiTinhSpinner, ngaySpinner, thangSpinner, namSpinner;
    private ShapeableImageView avt;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiHocTap apiHocTap;
    private String mediaPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        anhXa();

        // Setup dữ liệu cho các spinner ngày tháng năm và giới tính
        setUpDuLieuSpinner_GioiTinh();

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        //Them du lieu cho cac edittext va spinner
        getDataNguoiDung();

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
                        khoiEditHinh.setVisibility(View.VISIBLE);
                        avtEdit.setText(serverResponse.getName());

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

    private void getDataNguoiDung() {
        // tên, email, avt, sdt, ngaySinh, gioiTinh
        ten.setText(Utils.nguoiDungCurrent.getTennguoidung());
        email.setText(Utils.nguoiDungCurrent.getEmail());
        avtEdit.setText(Utils.nguoiDungCurrent.getAnhdaidien());
        sdt.setText(Utils.nguoiDungCurrent.getSodienthoai());
        Glide.with(this)
                .load(Utils.BASE_URL + "images/" + Utils.nguoiDungCurrent.getAnhdaidien())
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(avt);

        ngay.setText(Utils.nguoiDungCurrent.getNgaysinh());


        String str_gioiTinh = Utils.nguoiDungCurrent.getGioitinh();
        // Tìm vị trí của giới tính trong mảng gioiTinh
        int gioiTinhIndex = -1;
        String[] gioiTinhArray = {"Nam", "Nữ", "Khác"};
        for (int i = 0; i < gioiTinhArray.length; i++) {
            if (gioiTinhArray[i].equals(str_gioiTinh)) {
                gioiTinhIndex = i;
                break;
            }
        }
        // Đặt giá trị cho Spinner giới tính
        if (gioiTinhIndex != -1) {
            gioiTinhSpinner.setSelection(gioiTinhIndex);
        }
    }

    private void clickEvent() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ChinhSuaThongTinCaNhan.this)
                        .crop()
                        .compress(1204)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ChinhSuaThongTinCaNhan.this)
                        .crop()
                        .compress(1204)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hinhString = avtEdit.getText().toString().trim();
                String tenString = ten.getText().toString().trim();
                String emailString = email.getText().toString().trim();
                String sdtString = sdt.getText().toString().trim();
                String date = ngay.getText().toString().trim();

                String gioiTinh = gioiTinhSpinner.getSelectedItem().toString().trim();
                // Cập nhật thông tin cá nhân
                compositeDisposable.add(apiHocTap.updateThongTinNguoiDungAPI(tenString, emailString, sdtString, hinhString, gioiTinh, date, Utils.nguoiDungCurrent.getVaitro(), Utils.nguoiDungCurrent.getManguoidung())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                updateModel -> {
                                    if (updateModel.isSuccess()) {
                                        StyleableToast.makeText(ChinhSuaThongTinCaNhan.this, updateModel.getMessage() + "\nĐăng nhập lại để có hiệu quả tốt nhất!", R.style.success_toast).show();
                                    } else {
                                        StyleableToast.makeText(ChinhSuaThongTinCaNhan.this, updateModel.getMessage(), R.style.error_toast).show();
                                    }
                                },
                                throwable -> {
                                    // Xử lý ngoại lệ ở đây
                                    Log.e("A1", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                                }
                        ));

            }
        });
    }

    private void anhXa() {
        ten = findViewById(R.id.nameEdit);
        email = findViewById(R.id.emailEdit);
        sdt = findViewById(R.id.sdtEdit);
        avt = findViewById(R.id.avtEdit);

        editBtn = findViewById(R.id.editBtnEdit);
        backBtn = findViewById(R.id.backBtnEdit);

        pickDate = findViewById(R.id.pickDate);
        ngay = findViewById(R.id.ngaySinhCSTTCT);

        gioiTinhSpinner = findViewById(R.id.gioiTinhSpinner);

        editText = findViewById(R.id.txtEditPhoto);

        khoiEditHinh = findViewById(R.id.khoiEditPhoto);
        avtEdit = findViewById(R.id.avtEdittext);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.chinh_sua_thong_tin_ca_nhan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setUpDuLieuSpinner_GioiTinh() {
        String[] gioiTinh = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> gioiTinhAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gioiTinh);
        gioiTinhAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gioiTinhSpinner.setAdapter(gioiTinhAdapter);
    }


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}