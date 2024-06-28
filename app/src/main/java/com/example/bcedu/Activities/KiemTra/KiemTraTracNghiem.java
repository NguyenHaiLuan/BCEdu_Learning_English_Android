package com.example.bcedu.Activities.KiemTra;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bcedu.Activities.NguoiDung.SignIn;
import com.example.bcedu.Activities.TuVung.OnTapTuVung;
import com.example.bcedu.Adapter.BaiKiemTraAdapter;
import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.Dialog.DialogDanhHieu;
import com.example.bcedu.Dialog.DialogNoInternet;
import com.example.bcedu.Dialog.DialogThongBaoDiem;
import com.example.bcedu.Dialog.DialogTimeOver_KT;
import com.example.bcedu.Model.BaiKiemTra;
import com.example.bcedu.Model.CauTracNghiem;
import com.example.bcedu.Model.KetQuaKiemTra;
import com.example.bcedu.Model.KetQuaOnTap;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class KiemTraTracNghiem extends AppCompatActivity {
    private Button quit, confirm, dialogCancel, dialogQuit;
    private RadioButton op1, op2, op3, op4;
    private TextView time, cauHoi, viTriCauHoi, diem;
    private CountDownTimer countDownTimer;

    private Boolean doubleBack = false;
    private RadioGroup radioGroup;
    Dialog dialog;
    private long timeLeftInMillis;
    private int currentPosition = 0;
    private float score = 0;
    private float diemPerCau, diemHienTai;
    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BaiKiemTra baiKiemTra = new BaiKiemTra();
    private List<CauTracNghiem> danhSachCauTracNghiem = new ArrayList<>();
    private CauTracNghiem cauTracNghiem = new CauTracNghiem();
    private KetQuaKiemTra ketQuaKiemTra = new KetQuaKiemTra();
    private DialogAppLoading dialogAppLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiem_tra_trac_nghiem);

        initGiaoDien();
        anhXa();

        dialogAppLoading.show();

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        baiKiemTra = (BaiKiemTra) getIntent().getSerializableExtra("BaiKiemTra_GT");
        timeLeftInMillis = (long) baiKiemTra.getThoiluong()*60*1000;

        //lấy điểm hiện tại
        ketQuaKiemTra = (KetQuaKiemTra) getIntent().getSerializableExtra("diemHT69");
        diemHienTai = ketQuaKiemTra.getDiemso();

        // Lấy danh sách câu trắc nghiệm và xử lý sau khi tải xong
        getDanhSachCauTN();

        // Xử lí câu trắc nghiệm

        // Khởi động thời gian
        startTimer();
        clickEventListener();
    }

    private void hienThiCauHoiVaDapAn() {
        if (!danhSachCauTracNghiem.isEmpty()) {
            cauTracNghiem = danhSachCauTracNghiem.get(currentPosition);
            viTriCauHoi.setText("Câu " + (currentPosition + 1) + "/" + danhSachCauTracNghiem.size());
            float diemToanBai = 100;
            diemPerCau = diemToanBai/danhSachCauTracNghiem.size();
            getDataCauTracNghiem();
        } else {
            Log.e("KTTN", "Danh sách câu trắc nghiệm trống!");
        }
    }

    private void clickEventListener() {

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

                if (checkedRadioButtonId == -1) {
                    // Hiển thị thông báo yêu cầu người dùng chọn đáp án trước khi tiếp tục
                    StyleableToast.makeText(KiemTraTracNghiem.this, "Vui lòng chọn một đáp án!", R.style.warning).show();
                } else {
                    // Nếu người dùng đã chọn đáp án, kiểm tra và thực hiện các bước tiếp theo
                    kiemTraDapAn();
                    confirm.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            chuyenToiCauTiep();
                            getDataCauTracNghiem();
                            confirm.setVisibility(View.VISIBLE);
                        }
                    }, 1200);
                }
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        // cancel dialog
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // nút quit trong dialog
        dialogQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                dialog.dismiss();
            }
        });
    }

    private void kiemTraDapAn() {
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        RadioButton selectedRadioButton = findViewById(checkedRadioButtonId);
        String selectedAnswer = selectedRadioButton.getText().toString().trim();

        // Lấy đáp án đúng từ câu hỏi hiện tại
        String dapAnDung = cauTracNghiem.getDapandung().trim();
        String dapAnDung_str = "";

        // Kiểm tra đáp án
        switch (dapAnDung) {
            case "A":
                dapAnDung_str = op1.getText().toString().trim();
                break;
            case "B":
                dapAnDung_str = op2.getText().toString().trim();
                break;
            case "C":
                dapAnDung_str = op3.getText().toString().trim();
                break;
            case "D":
                dapAnDung_str = op4.getText().toString().trim();
                break;
        }

        boolean isCorrect = selectedAnswer.equals(dapAnDung_str);

        if (isCorrect) {
            StyleableToast.makeText(this, "Chính xác!", R.style.chinhXacToast).show();
            selectedRadioButton.setTextColor(Color.GREEN); // Đổi màu xanh cho đáp án đã chọn
            score += diemPerCau; // Tăng điểm số lên
            diem.setText("Điểm: " + score); // Cập nhật điểm số
        } else {
            StyleableToast.makeText(this, "Sai rồi!", R.style.saiToast).show();
            selectedRadioButton.setTextColor(Color.RED); // Đổi màu đỏ cho đáp án đã chọn
            // Tìm và đổi màu xanh cho đáp án đúng
            switch (dapAnDung) {
                case "A":
                    op1.setTextColor(Color.GREEN);
                    break;
                case "B":
                    op2.setTextColor(Color.GREEN);
                    break;
                case "C":
                    op3.setTextColor(Color.GREEN);
                    break;
                case "D":
                    op4.setTextColor(Color.GREEN);
                    break;
            }
        }

        // Sử dụng Handler để chuyển tới câu tiếp theo sau 1 giây
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Xóa màu nền cho tất cả các đáp án
                op1.setTextColor(Color.BLACK);
                op2.setTextColor(Color.BLACK);
                op3.setTextColor(Color.BLACK);
                op4.setTextColor(Color.BLACK);
            }
        }, 1200);
    }

    private void getDanhSachCauTN() {
        compositeDisposable.add(apiHocTap.getCauHoiTracNghiem(baiKiemTra.getMabaikiemtra())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        baiKiemTraModel -> {
                            if (baiKiemTraModel.isSuccess()) {
                                danhSachCauTracNghiem = baiKiemTraModel.getResult();
                                hienThiCauHoiVaDapAn();
                                dialogAppLoading.cancel();
                            }
                            else {
                                dialogAppLoading.cancel();
                                Toast.makeText(this, "Không lấy được danh sách câu trắc nghiệm!", Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> {
                            dialogAppLoading.cancel();
                            StyleableToast.makeText(this, "Đã có lỗi xảy ra! Thử khởi động lại sau vài phút!", R.style.error_toast).show();
                            Log.e("KTTN getList câu trắc nghiệm:", "AAA: " + throwable.getMessage());
                        }
                ));
    }

    private void chuyenToiCauTiep() {
        if (currentPosition == danhSachCauTracNghiem.size() - 2) {
            confirm.setText("Xong");
        }

        if (currentPosition < danhSachCauTracNghiem.size() - 1) {
            currentPosition++;
            cauTracNghiem = danhSachCauTracNghiem.get(currentPosition);
            getDataCauTracNghiem();
            radioGroup.clearCheck();
            viTriCauHoi.setText("Câu " + (currentPosition + 1) + "/" + danhSachCauTracNghiem.size());
        } else {
            if (score > diemHienTai) {
                dialogAppLoading.show();
                upDiemLenServer();
            }
            StyleableToast.makeText(getApplicationContext(), "Điểm của bạn: " + score, R.style.thongBaoDiem).show();
            finish();
        }
    }

    private void upDiemLenServer(){
        compositeDisposable.add(apiHocTap.capnhatDiemCaoNhatBaiKiemTraAPI(score, ketQuaKiemTra.getMaketquakiemtra())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        ketQuaOnTapModel -> {
                            if (ketQuaOnTapModel.isSuccess()) {
                                dialogAppLoading.cancel();
                                StyleableToast.makeText(this, "Đã cập nhật điểm lên server!", R.style.success_toast).show();
                            } else {
                                dialogAppLoading.cancel();
                                StyleableToast.makeText(this, "Có lỗi khi cập nhật điểm lên server!", R.style.error_toast).show();
                            }
                        },
                        throwable -> {
                            dialogAppLoading.cancel();
                            StyleableToast.makeText(this, "Đã có lỗi xảy ra! Thử khởi động lại sau vài phút!", R.style.error_toast).show();
                            // Xử lý ngoại lệ ở đây
                            Log.e("A1", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                        }
                ));
    }


    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                showCustomDialog();
            }
        }.start();
    }

    private void getDataCauTracNghiem(){
        cauHoi.setText(cauTracNghiem.getCauhoi().toString().trim());
        op1.setText(cauTracNghiem.getDapana().toString().trim());
        op2.setText(cauTracNghiem.getDapanb().toString().trim());
        op3.setText(cauTracNghiem.getDapanc().toString().trim());
        op4.setText(cauTracNghiem.getDapand().toString().trim());
    }

    private void updateCountdownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        time.setText(timeLeftFormatted);
    }

    private void showCustomDialog() {
        DialogTimeOver_KT dialogTimeOver = new DialogTimeOver_KT(KiemTraTracNghiem.this, KiemTraTracNghiem.this);

        dialogTimeOver.setCancelable(false);
        dialogTimeOver.getWindow().setBackgroundDrawable(
                new ColorDrawable(ContextCompat.getColor(KiemTraTracNghiem.this, android.R.color.transparent)));
        dialogTimeOver.show();
    }

    private void anhXa() {
        time = findViewById(R.id.thoiGian_KTTN);
        cauHoi = findViewById(R.id.cauhoi_KTTN);
        viTriCauHoi = findViewById(R.id.thuTuCauHoi_KTTN);
        diem = findViewById(R.id.diem_KTTN);

//        loa = findViewById(R.id.loabtn);
        quit = findViewById(R.id.btnQuit_KTTN);
        confirm = findViewById(R.id.btnconfirm_KTTN);

        dialog = new Dialog(KiemTraTracNghiem.this);
        dialog.setContentView(R.layout.dialog_confirm_quit);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_custom_bg));
        dialog.setCancelable(false);

        dialogQuit = dialog.findViewById(R.id.confirmThoatBtn);
        dialogCancel = dialog.findViewById(R.id.huyThoatBtn);

        op1 = findViewById(R.id.dapAnA_KTTN);
        op2 = findViewById(R.id.dapAnB_KTTN);
        op3 = findViewById(R.id.dapAnC_KTTN);
        op4 = findViewById(R.id.dapAnD_KTTN);

        radioGroup = findViewById(R.id.radiochoices_KTTN);

        dialogAppLoading = new DialogAppLoading(this);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kiem_tra_trac_nghiem);
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBack) {
            super.onBackPressed();
            //thoát ra Login
            finish();
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