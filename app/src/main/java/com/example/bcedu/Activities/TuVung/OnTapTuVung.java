package com.example.bcedu.Activities.TuVung;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

import com.example.bcedu.Dialog.DialogDanhHieu;
import com.example.bcedu.Model.KetQuaOnTap;
import com.example.bcedu.Model.TuVung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Service.MusicService;
import com.example.bcedu.Utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OnTapTuVung extends AppCompatActivity{
    private ImageButton loa;
    private Button quit, confirm, dialogCancel, dialogQuit;
    private RadioButton op1, op2, op3, op4;
    private TextView time, cauHoi, viTriCauHoi, diem, tuLoai,phienAm;
    private RadioGroup radioGroup;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable runnable;
    private CountDownTimer countDownTimer;
    private long thoiGianConLai, tongThoiGian;
    Dialog dialog;
    private TuVung tuVung;
    private List<TuVung> danhSachTuVung;
    private int currentPosition;
    private float score = 0;
    private final float TOTAL_SCORE = 100;
    private float diemPerCau;
    private Boolean doubleBack = false;
    private ImageButton musicButton;
    private boolean isPlaying = false;

    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private float diemHienTai;
    private int status, maxephang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        anhxa();

        // Dừng dịch vụ phát nhạc nền
        Intent musicIntent = new Intent(this, MusicService.class);
        stopService(musicIntent);

        int soXN = 0;
        soXN = getIntent().getIntExtra("soXacNhan", 0);

        if (soXN == 69){
            danhSachTuVung = (List<TuVung>) getIntent().getSerializableExtra("danhSachTuVungA");
            // Lấy từ vựng đầu tiên
            tuVung = danhSachTuVung.get(currentPosition);
        } else {
            tuVung = (TuVung) getIntent().getSerializableExtra("tuVung");
            danhSachTuVung = (List<TuVung>) getIntent().getSerializableExtra("danhSachTuVung");
        }

        if (danhSachTuVung == null || danhSachTuVung.isEmpty()) {
            Log.e("AAAAA", "Danh sách từ vựng trống!");
            return;
        }

        maxephang = Utils.nguoiDungCurrent.getMaxephang();

        getDiemHienTai();
        // Load dữ liệu cho từ vựng
        getDataTuVung();
        thietLapDapAn();

        // Khởi tạo thời gian (Thời gian còn lại)
        tongThoiGian = danhSachTuVung.size() * 45000; // 1 câu hỏi có 45 giây để trả lời
        thoiGianConLai = tongThoiGian;
        startTimer();
        score = 0;
        diemPerCau = TOTAL_SCORE / danhSachTuVung.size();

        // Bắt sự kiện cho các button
        eventClick();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

                if (checkedRadioButtonId == -1) {
                    // Hiển thị thông báo yêu cầu người dùng chọn đáp án trước khi tiếp tục
                    StyleableToast.makeText(OnTapTuVung.this, "Vui lòng chọn một đáp án!", R.style.warning).show();
                } else {
                    // Nếu người dùng đã chọn đáp án, kiểm tra và thực hiện các bước tiếp theo
                    kiemTraDapAn();
                    confirm.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            chuyenToiCauTiep();
                            thietLapDapAn();
                            confirm.setVisibility(View.VISIBLE);
                        }
                    }, 1800);
                }
            }
        });

    }


    private void getDataTuVung() {
        // Lấy từ vựng tại vị trí hiện tại
        tuVung = danhSachTuVung.get(currentPosition);

        // Hiển thị từ vựng và câu hỏi tương ứng
        cauHoi.setText(tuVung.getTentuvung());
        tuLoai.setText("("+tuVung.getLoaituvung()+")");
        phienAm.setText("/"+tuVung.getPhatam()+"/");

        viTriCauHoi.setText("Câu " + (currentPosition + 1) + "/" + danhSachTuVung.size());
    }

    private void kiemTraDapAn() {
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        RadioButton checkedRadioButton = findViewById(checkedRadioButtonId);
        String luaChonCuaBan = checkedRadioButton.getText().toString().trim();
        String dapAnDung = tuVung.getYnghia().trim();

        if (luaChonCuaBan.equals(dapAnDung)) {
            StyleableToast.makeText(this, "Chính xác!", R.style.chinhXacToast).show();
            // Nếu đáp án được chọn là đúng
            checkedRadioButton.setTextColor(Color.GREEN); // Đổi màu xanh cho đáp án đúng
            score += diemPerCau; // Tăng điểm số lên
            diem.setText("Điểm: " + score); // Cập nhật điểm số
        } else {
            StyleableToast.makeText(this, "Sai rồi!", R.style.saiToast).show();
            // Nếu đáp án được chọn là sai
            checkedRadioButton.setTextColor(Color.RED); // Đổi màu đỏ cho đáp án đã chọn
            // Tìm và đổi màu xanh cho đáp án đúng
            if (dapAnDung.equals(op1.getText().toString().trim())) {
                op1.setTextColor(Color.GREEN);
            } else if (dapAnDung.equals(op2.getText().toString().trim())) {
                op2.setTextColor(Color.GREEN);
            } else if (dapAnDung.equals(op3.getText().toString().trim())) {
                op3.setTextColor(Color.GREEN);
            } else if (dapAnDung.equals(op4.getText().toString().trim())) {
                op4.setTextColor(Color.GREEN);
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

    private void thietLapDapAn() {
        // Lấy nghĩa của từ hiện tại và loại bỏ khoảng trắng
        String nghiaTuHienTai = tuVung.getYnghia().trim();

        // Tạo danh sách các nghĩa từ danh sách từ vựng và loại bỏ khoảng trắng
        List<String> danhSachNghia = new ArrayList<>();
        for (TuVung tu : danhSachTuVung) {
            danhSachNghia.add(tu.getYnghia().trim());
        }

        // Loại bỏ nghĩa của từ hiện tại khỏi danh sách nghĩa
        danhSachNghia.remove(nghiaTuHienTai);

        // Trộn ngẫu nhiên danh sách nghĩa
        Collections.shuffle(danhSachNghia);

        // Lấy ra 3 nghĩa ngẫu nhiên từ danh sách đã trộn
        String dapAnSai1 = danhSachNghia.get(0);
        String dapAnSai2 = danhSachNghia.get(1);
        String dapAnSai3 = danhSachNghia.get(2);

        // Đặt các đáp án vào một danh sách để dễ dàng truy cập
        List<String> danhSachDapAn = new ArrayList<>();
        danhSachDapAn.add(nghiaTuHienTai);
        danhSachDapAn.add(dapAnSai1);
        danhSachDapAn.add(dapAnSai2);
        danhSachDapAn.add(dapAnSai3);

        // Trộn ngẫu nhiên danh sách đáp án
        Collections.shuffle(danhSachDapAn);

        // Đặt các đáp án vào các radio button
        op1.setText(danhSachDapAn.get(0));
        op2.setText(danhSachDapAn.get(1));
        op3.setText(danhSachDapAn.get(2));
        op4.setText(danhSachDapAn.get(3));
    }

    private void chuyenToiCauTiep() {
        // Tới câu cuoi cung thì set text cho button
        if (currentPosition == danhSachTuVung.size() - 2) {
            confirm.setText("Xong");
        }

        // Lấy dữ liệu câu hỏi và thiết lập dap an cho từ vựng
        if (currentPosition < danhSachTuVung.size() - 1) {
            currentPosition++;
            getDataTuVung();
            thietLapDapAn();
            radioGroup.clearCheck(); // Xóa lựa chọn radio button
            // Tới câu cuối thì
        } else {
            // Nếu đã được mức rank này thì...
            if (status == 1){
                StyleableToast.makeText(this, "Bạn đã hoàn thành tất cả các từ vựng!\nTổng điểm: " + score, R.style.thongBaoDiem).show();
                finish();
            } else { // Nếu chưa đạt được mức rank này
                //Nếu diem max
                if (score == 100) {
                    // Hiện dialog đạt được rank mới
                    DialogDanhHieu dialogDanhHieu = new DialogDanhHieu(OnTapTuVung.this, OnTapTuVung.this);
                    dialogDanhHieu.setCancelable(false);
                    dialogDanhHieu.getWindow().setBackgroundDrawable(
                            new ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)));
                    dialogDanhHieu.show();

                    //Cập nhật trạng thái và lên server
                    status = 1;
                    capNhatDiemLenServer();

                    //Tăng rank lên 1 và cập nhật
                    if (maxephang <11)
                        maxephang++;
                    capNhatRank();
                }
                //neu diem lơn hon diem hien tại những vẫn chưa đạt 100
                if (score<100 && score>diemHienTai) {
                    capNhatDiemLenServer();
                    finish();
                }
//
                // nếu điểm nhỏ hơn điểm hiện tại
                 if (score <= diemHienTai){
                     StyleableToast.makeText(this, "Bạn đã hoàn thành tất cả các từ vựng!\nTổng điểm: " + score, R.style.thongBaoDiem).show();
                     finish();
                 }

            }
        }
    }

    private void capNhatRank() {
        compositeDisposable.add(apiHocTap.updateXepHangAPI(maxephang, Utils.nguoiDungCurrent.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        modelCapNhatRank -> {
                            if (modelCapNhatRank.isSuccess()) {
                                StyleableToast.makeText(getApplicationContext(), modelCapNhatRank.getMessage(), R.style.success_toast).show();
                            } else {
                                StyleableToast.makeText(OnTapTuVung.this, modelCapNhatRank.getMessage(), Toast.LENGTH_SHORT, R.style.error_toast).show();
                            }
                        }, throwable -> {
                            StyleableToast.makeText(OnTapTuVung.this, "Chưa thể cập nhật rank ở server! Lỗi: " + throwable.getMessage(), R.style.error_toast).show();
                            Log.e("Loi", "post_rank: " + throwable.getMessage());
                        }
                ));
    }
    // update điểm lên server
    private void capNhatDiemLenServer() {
        compositeDisposable.add(apiHocTap.updateDiemCaoNhatAPI(score, tuVung.getMacapdotuvung(), Utils.nguoiDungCurrent.getManguoidung(), status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        capNhatDiemModel -> {
                            if (capNhatDiemModel.isSuccess()) {
                                StyleableToast.makeText(getApplicationContext(), capNhatDiemModel.getMessage(), R.style.success_toast).show();
                            } else {
                                StyleableToast.makeText(OnTapTuVung.this, capNhatDiemModel.getMessage(), Toast.LENGTH_SHORT, R.style.error_toast).show();
                            }
                        }, throwable -> {
                            StyleableToast.makeText(OnTapTuVung.this, "Chưa thể cập nhật điểm ở server! Lỗi: " + throwable.getMessage(), R.style.error_toast).show();
                            Log.e("Loi", "postdiem: " + throwable.getMessage());
                        }
                ));
    }

    // get điểm hiện tại có mã cấp độ và mã người dùng
    private void getDiemHienTai() {
        compositeDisposable.add(apiHocTap.getKetQuaOnTapAPI(tuVung.getMacapdotuvung(), Utils.nguoiDungCurrent.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        ketQuaOnTapModel -> {
                            if (ketQuaOnTapModel.isSuccess()) {
                                KetQuaOnTap ketQuaOnTap = ketQuaOnTapModel.getResult().get(0);
                                diemHienTai = ketQuaOnTap.getDiemcaonhat();
                                status = ketQuaOnTap.getStatus();
                            }
                        },
                        throwable -> {
                            // Xử lý ngoại lệ ở đây
                            Log.e("A1", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                        }
                ));
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(thoiGianConLai, 1000) {
            @Override
            public void onTick(long thoiGianChoDenKhiKetThuc) {
                thoiGianConLai = thoiGianChoDenKhiKetThuc;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                // Thực hiện các hành động sau khi thời gian kết thúc
                time.setText("00:00");
                com.example.bcedu.Dialog.DialogTimeOver dialogTimeOver = new com.example.bcedu.Dialog.DialogTimeOver(OnTapTuVung.this, OnTapTuVung.this);

                dialogTimeOver.setCancelable(false);
                dialogTimeOver.getWindow().setBackgroundDrawable(
                        new ColorDrawable(ContextCompat.getColor(OnTapTuVung.this, android.R.color.transparent)));
                dialogTimeOver.show();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (thoiGianConLai / 1000) / 60;
        int seconds = (int) (thoiGianConLai / 1000) % 60;
        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        time.setText(timeLeftFormatted);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void eventClick() {

        // Bắt sự kiện cho nút quit
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
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

        // cancel dialog
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        loa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay();
                String audioUrl = tuVung.getAudio();
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioUrl);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.start();

            }
        });
    }

    protected void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
        }
    }

    private void anhxa() {
        time = findViewById(R.id.txttimeTN);
        cauHoi = findViewById(R.id.txtquestionTN);
        viTriCauHoi = findViewById(R.id.txtquestcountTN);
        diem = findViewById(R.id.txtDiemTN);
        tuLoai = findViewById(R.id.tuLoaiOnTap);
        phienAm = findViewById(R.id.phienAmOnTap);

        loa = findViewById(R.id.loabtn);
        quit = findViewById(R.id.btnQuitTN);
        confirm = findViewById(R.id.btnconfirmTN);

        dialog = new Dialog(OnTapTuVung.this);
        dialog.setContentView(R.layout.dialog_confirm_quit);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_custom_bg));
        dialog.setCancelable(false);

        dialogQuit = dialog.findViewById(R.id.confirmThoatBtn);
        dialogCancel = dialog.findViewById(R.id.huyThoatBtn);

        op1 = findViewById(R.id.op1);
        op2 = findViewById(R.id.op2);
        op3 = findViewById(R.id.op3);
        op4 = findViewById(R.id.op4);

        radioGroup = findViewById(R.id.radiochoices);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_tap_tu_vung);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBack) {
            super.onBackPressed();
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