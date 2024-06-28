package com.example.bcedu.Activities.TuVung;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.Model.TuVung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Service.MusicService;
import com.example.bcedu.Utils.Utils;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChiTietTuVung extends AppCompatActivity {
    private DialogAppLoading dialogAppLoading;
    private TextView tenTuVung, dichNghia, viDu, level, phienAm, tuLoai;
    private ImageView anhMinhHoa;
    private ImageButton nextbtn, backbtn, savebtn, testbtn, docTuVungBtn, listbtn;

    private List<TuVung> danhSachTuVung;
    private int currentPosition;

    TuVung tuVung;
    private RelativeLayout tuVungView;

    private MediaPlayer mediaPlayer;

    private Handler handler;
    private Runnable runnable;
    float diemHienTai;

    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        anhXa();
        tuVung = (TuVung) getIntent().getSerializableExtra("chitiet");
        diemHienTai = getIntent().getFloatExtra("diemHienTai", 1);
        danhSachTuVung = (List<TuVung>) getIntent().getSerializableExtra("danhSachTuVung");

        // Kiêm tra nếu list từ vưng null hoặc rong
        if (danhSachTuVung == null || danhSachTuVung.isEmpty()) {
            Log.e("ChiTietTuVung", "Empty danhSachTuVung");
            return;
        }

        currentPosition = getIntent().getIntExtra("position", 0);

        getDataTuVung(currentPosition);

        onClickReturnListBtn();
        onClickNextBtn();
        onClickBackBtn();
        onClickSpeakerBtn();
        onClickOnTapBtn();

        if (Utils.nguoiDungCurrent.getHdchitiettuvung() == 0){
            huongDanSuDungCTTV();
        }

    }



    private void onClickOnTapBtn() {
        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (danhSachTuVung.size() < 4){
                    StyleableToast.makeText(ChiTietTuVung.this, "Phải có trên 4 từ vựng để có thể ôn tập", R.style.warning).show();
                }else {
                    Intent intent = new Intent(ChiTietTuVung.this, OnTapTuVung.class);
                    intent.putExtra("danhSachTuVung", (Serializable) danhSachTuVung);
                    intent.putExtra("tuVung", tuVung);
                    intent.putExtra("diemht", diemHienTai);
                    startActivity(intent);
                }
            }
        });
    }

    private void onClickSpeakerBtn() {
        docTuVungBtn.setOnClickListener(new View.OnClickListener() {
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
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            if(handler!=null){
                handler.removeCallbacks(runnable);
            }
        }
    }

    private void onClickBackBtn() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition > 0) {
                    currentPosition--;

                    getDataTuVung(currentPosition);
                } else {
                    StyleableToast.makeText(ChiTietTuVung.this, "Đã là đầu của danh sách rồi bạn ơi, tới cuối danh sách lại nhé!", R.style.warning).show();

                    // Chuyển tới cuối danh sách
                    currentPosition = danhSachTuVung.size() - 1;
                    getDataTuVung(currentPosition);
                }
            }
        });
    }


    // Chuyển tới từ vựng tiếp theo
    private void onClickNextBtn() {
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition < danhSachTuVung.size() - 1) {
                    currentPosition++;
                    getDataTuVung(currentPosition);
                } else {
                    // Đến cuối danh sách
                    StyleableToast.makeText(ChiTietTuVung.this, "Bạn đã học hết từ vựng, chuyển về từ vựng đầu tiên!", R.style.warning).show();

                    // chuyển về vị trí đầu tiên trong danh sách
                    currentPosition = 0;
                    getDataTuVung(currentPosition);
                }
            }
        });
    }

    // Nút quay trở lại list từ vựng
    private void onClickReturnListBtn() {
        listbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Load ra chi tiết từ vựng dựa trên vị trí
    private void getDataTuVung(int i) {
        tuVung = danhSachTuVung.get(i);

        tenTuVung.setText(tuVung.getTentuvung());
        dichNghia.setText(tuVung.getYnghia());
        viDu.setText(tuVung.getViduminhhoa());
        phienAm.setText("/"+tuVung.getPhatam()+"/");
        tuLoai.setText(tuVung.getLoaituvung());
        Glide.with(this).load(tuVung.getHinhanhmota()).into(anhMinhHoa);

        String audio = tuVung.getAudio();
    }

    private void anhXa() {
        tenTuVung = findViewById(R.id.tuVungChiTiet);
        dichNghia = findViewById(R.id.nghiaTuVungChiTiet);
        viDu = findViewById(R.id.viDuTuVung);
        level = findViewById(R.id.tuVungChiTiet);
        phienAm = findViewById(R.id.PhienAmTuVung);
        tuLoai = findViewById(R.id.tuLoaiTuVung);

        anhMinhHoa = findViewById(R.id.anhMinhHoaTuVung);

        nextbtn = findViewById(R.id.tuVungKeTiep);
        backbtn = findViewById(R.id.tuVungTruoc);
        testbtn = findViewById(R.id.kiemTraTuVung);
        docTuVungBtn = findViewById(R.id.phatAmRaLoa);
        listbtn = findViewById(R.id.troLaiListTuVung);

        tuVungView = findViewById(R.id.layoutTuVung);

        dialogAppLoading = new DialogAppLoading(this);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chi_tiet_tu_vung);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



    private void reloadDuLieu(){
        compositeDisposable.add(apiHocTap.dangNhapAPI(Utils.nguoiDungCurrent.getEmail(), Utils.nguoiDungCurrent.getMatkhau())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dangNhapHocVienModel -> {
                            if (dangNhapHocVienModel.isSuccess()){
                                Utils.nguoiDungCurrent = dangNhapHocVienModel.getResult().get(0);
                                getBacRank();
                                getBacRankTiepTheo();
                            }
                            else {
                                StyleableToast.makeText(getApplicationContext(), dangNhapHocVienModel.getMessage(), R.style.error_toast).show();
                                Log.d("log_CTTV1", dangNhapHocVienModel.getMessage());
                            }
                        }, throwable ->{
                            Log.d("log_CTTV", throwable.getMessage());
                        }
                ));
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
                            Log.e("Loi", "get_xephang2: " + throwable.getMessage());
                        }
                ));
    }

    private void huongDanSuDungCTTV() {
        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(tenTuVung, "Từ vựng", "Tên từ vựng ở đây!")
                                .outerCircleColor(R.color.xanhduong)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(25)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(16)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.DEFAULT)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .icon(ContextCompat.getDrawable(this, R.drawable.ic_vocabulary))
                                .targetRadius(60),

                        TapTarget.forView(tuLoai, "Loại từ", "Bạn xem loại từ vựng ở đây, có thể ấn vào nó để khám phá ý nghĩa của từ vựng!")
                                .outerCircleColor(R.color.xanhduong)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(25)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(16)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.DEFAULT)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .icon(ContextCompat.getDrawable(this, R.drawable.ic_spacefill))
                                .targetRadius(60),

                        TapTarget.forView(listbtn, "Quay trở lại!", "Click vào đây để trở về danh sách từ vựng!")
                                .outerCircleColor(R.color.xanhduong)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(25)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(16)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.DEFAULT)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .icon(ContextCompat.getDrawable(this, R.drawable.baseline_format_list_bulleted_24))
                                .targetRadius(60),

                        TapTarget.forView(testbtn, "Kiểm tra", "Ôn tập từ vựng liền cho nóng! Tớ sẽ mang đến cho cậu bài trắc nghiệm ôn tập ngay khi cậu ấn vào đây!")
                                .outerCircleColor(R.color.xanhduong)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(25)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(16)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.DEFAULT)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .icon(ContextCompat.getDrawable(this, R.drawable.baseline_star_24))
                                .targetRadius(60),

                        TapTarget.forView(docTuVungBtn, "Speaker", "Âm thanh giúp dễ nhớ hơn đúng không!")
                                .outerCircleColor(R.color.xanhduong)
                                .outerCircleAlpha(0.96f)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(25)
                                .titleTextColor(R.color.white)
                                .descriptionTextSize(16)
                                .descriptionTextColor(R.color.white)
                                .textColor(R.color.white)
                                .textTypeface(Typeface.DEFAULT)
                                .dimColor(R.color.black)
                                .drawShadow(true)
                                .cancelable(false)
                                .tintTarget(true)
                                .transparentTarget(false)
                                .icon(ContextCompat.getDrawable(this, R.drawable.baseline_volume_up_24))
                                .targetRadius(60)).listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        compositeDisposable.add(apiHocTap.hoanThanhHuongDanCTTV_API(Utils.nguoiDungCurrent.getManguoidung())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        hoanthanhModel -> {
                                            if (hoanthanhModel.isSuccess()) {
                                                StyleableToast.makeText(getApplicationContext(), hoanthanhModel.getMessage(), R.style.thongBaoDiem).show();
                                            } else {
                                            }
                                        }, throwable -> {
                                            Log.e("Loi", "get_xephang: " + throwable.getMessage());
                                        }
                                ));
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                }).start();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bắt đầu lại dịch vụ phát nhạc nền khi Activity được tiếp tục
        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);
        reloadDuLieu();
    }

}