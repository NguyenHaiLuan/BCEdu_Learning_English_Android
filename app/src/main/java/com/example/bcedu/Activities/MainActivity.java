package com.example.bcedu.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.bcedu.Activities.Admin.AdminMain;
import com.example.bcedu.Activities.KhoaHoc.KhoaHocActivity;
import com.example.bcedu.Activities.KhoaHoc.LobbyKhoaHoc;
import com.example.bcedu.Activities.KhoaHoc.QuanLyKhoaHoc;
import com.example.bcedu.Activities.KhoaHoc.QuanLyKhoaHocDaThamGia;
import com.example.bcedu.Activities.NguoiDung.SignIn;
import com.example.bcedu.Activities.NguoiDung.ThongTinCaNhan;
import com.example.bcedu.Activities.TuVung.CapDoTuVung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Service.MusicService;
import com.example.bcedu.Utils.Utils;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private CardView khoaHocCard, hocTuVungCard, adminCard, cardToi, cardXepHang, cardHuongDan;
    private Toolbar toolbar;
    private NavigationView navigation;
    private DrawerLayout drawerLayout;
    private Boolean doubleBack = false;
    private ImageButton toolbarBtn;

    private TextView tenNguoiDung, vaiTroNav, rank;
    private ShapeableImageView avt, rankFrame;
    private MediaPlayer mediaPlayer;

    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGiaoDien();
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        anhXa();
        // Xử lí navigation
        initNavigation();

        // Bắt đầu MusicService
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        startService(intent);

        mediaPlayer = MediaPlayer.create(this, R.raw.click);

        // Bắt sự kiện các cardview
        clickEventBtn();

        // Hiện T
        if (Utils.nguoiDungCurrent.getHdmain() == 0){
            huongDanSuDungMain();
        }
    }

    private void clickEventBtn() {

        khoaHocCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LobbyKhoaHoc.class);
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
                startActivity(intent);
            }
        });

        hocTuVungCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CapDoTuVung.class);
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
                startActivity(intent);
            }
        });

        adminCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if (Utils.nguoiDungCurrent.getVaitro().equalsIgnoreCase("Admin")) {
                    StyleableToast.makeText(getApplicationContext(), "Chào mừng Admin " + Utils.nguoiDungCurrent.getTennguoidung(), R.style.thongBaoDiem).show();
                    Intent intent = new Intent(MainActivity.this, AdminMain.class);
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                    startActivity(intent);
                } else {
                    StyleableToast.makeText(getApplicationContext(), "Chỉ có Admin mới có quyền truy cập tác vụ này!", R.style.warning).show();
                    // Re-enable the button after the toast duration
                    int toastDuration = 2000; // 2 seconds for Toast.LENGTH_SHORT
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setEnabled(true);
                        }
                    }, toastDuration);
                }
            }
        });

        cardToi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ThongTinCaNhan.class);
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
                startActivity(intent);
            }
        });

        cardXepHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, XepHangAll.class);
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
                startActivity(intent);
            }
        });

        cardHuongDan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutUs.class);
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
                startActivity(intent);
            }
        });

        toolbarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });
    }

    private void initNavigation() {
        View headerView = navigation.getHeaderView(0);

        avt = headerView.findViewById(R.id.avtNguoiDungNav);
        tenNguoiDung = headerView.findViewById(R.id.tenNguoiDungNav);
        vaiTroNav = headerView.findViewById(R.id.vaiTroNav);
        rank = headerView.findViewById(R.id.rankNav);
        rankFrame = headerView.findViewById(R.id.hinhRankNav);

        rank.setText(Utils.xepHangCuaToi.getTenxephang());
        Glide.with(this).load(Utils.xepHangCuaToi.getHinhanh()).into(rankFrame);

        Glide.with(this)
                .load(Utils.BASE_URL + "images/" + Utils.nguoiDungCurrent.getAnhdaidien())
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(avt);

        tenNguoiDung.setText(Utils.nguoiDungCurrent.getTennguoidung());
        vaiTroNav.setText(Utils.nguoiDungCurrent.getVaitro());


        //navigation drawer
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.nav_home) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (itemId == R.id.nav_admin) {
                    if (Utils.nguoiDungCurrent.getVaitro().equals("Admin")) {
                        StyleableToast.makeText(getApplicationContext(), "Chào mừng Admin " + Utils.nguoiDungCurrent.getTennguoidung(), R.style.thongBaoDiem).show();
                        Intent intentAdmin = new Intent(getApplicationContext(), AdminMain.class);
                        startActivity(intentAdmin);
                    } else {
                        StyleableToast.makeText(getApplicationContext(), "Chỉ có Admin mới có quyền truy cập!", R.style.warning).show();
                    }

                }
                if (itemId == R.id.nav_hocTuVung) {
                    Intent intentTuVung = new Intent(getApplicationContext(), CapDoTuVung.class);
                    startActivity(intentTuVung);
                }
                if (itemId == R.id.nav_thongTinTaiKhoan) {
                    Intent intent = new Intent(getApplicationContext(), ThongTinCaNhan.class);
                    startActivity(intent);
                }
                if (itemId == R.id.nav_xepHang) {
                    Intent intentKiemTra = new Intent(getApplicationContext(), XepHangAll.class);
                    startActivity(intentKiemTra);
                }
                if (itemId == R.id.nav_logout) {
                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                    startActivity(intent);
                    finish();
                }
                if (itemId == R.id.nav_khoaHoc) {
                    Intent intent = new Intent(getApplicationContext(), KhoaHocActivity.class);
                    startActivity(intent);
                }
                if (itemId == R.id.nav_khoaHocCuaToi) {
                    Intent intent = new Intent(getApplicationContext(), QuanLyKhoaHocDaThamGia.class);
                    startActivity(intent);
                }

                if (itemId == R.id.nav_caidat) {
                    Intent intent = new Intent(getApplicationContext(), Setting.class);
                    startActivity(intent);
                }
                if (itemId == R.id.nav_khoaHocDaTao) {
                    if (Utils.nguoiDungCurrent.getVaitro().equalsIgnoreCase("Giảng Viên") || Utils.nguoiDungCurrent.getVaitro().equalsIgnoreCase("Admin")) {
                        Intent intentAdmin = new Intent(getApplicationContext(), QuanLyKhoaHoc.class);
                        startActivity(intentAdmin);
                    } else {
                        StyleableToast.makeText(getApplicationContext(), "Chỉ có Giảng viên mới có quyền truy cập!", R.style.warning).show();
                    }
                }
                return false;
            }
        });
    }

    private void anhXa() {
        khoaHocCard = findViewById(R.id.khoahoc);
        hocTuVungCard = findViewById(R.id.hocTuVungCard);
        adminCard = findViewById(R.id.kiemTraCard);
        cardToi = findViewById(R.id.myProfileCardView);
        cardXepHang = findViewById(R.id.rankCardView);
        cardHuongDan = findViewById(R.id.huongDanCardView);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerMain);
        navigation = findViewById(R.id.navigationView);

        toolbarBtn = findViewById(R.id.toolbarBtn);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



    private void reloadDuLieu() {
        compositeDisposable.add(apiHocTap.dangNhapAPI(Utils.nguoiDungCurrent.getEmail(), Utils.nguoiDungCurrent.getMatkhau())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        dangNhapHocVienModel -> {
                            if (dangNhapHocVienModel.isSuccess()) {
                                Utils.nguoiDungCurrent = dangNhapHocVienModel.getResult().get(0);
                                getBacRank();
                                getBacRankTiepTheo();
                            } else {
                                StyleableToast.makeText(getApplicationContext(), dangNhapHocVienModel.getMessage(), R.style.error_toast).show();
                                Log.d("log_CTTV1", dangNhapHocVienModel.getMessage());
                            }
                        }, throwable -> {
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

    private void huongDanSuDungMain() {
        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(toolbarBtn, "Thanh công cụ", "Thao tác dễ dàng hơn với thanh công cụ ở đây. Trượt từ trái sang phải để sử dụng bạn nhé")
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

                        TapTarget.forView(hocTuVungCard, "Học từ vựng!", "Chứa kho tàng từ vựng của chúng tôi. Thăng hạng khi học xong đấy bạn nhé!")
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

                        TapTarget.forView(cardToi, "Tôi", "Tất cả mọi thứ liên quan tới bạn đều ở đây!\n Hãy chỉnh sửa thật đẹp nào!")
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
                                .icon(ContextCompat.getDrawable(this, R.drawable.baseline_person_24))
                                .targetRadius(60),

                        TapTarget.forView(cardXepHang, "Xếp hạng", "Bảng xếp hạng dành cho những thành viên kì cựu nhất\n Đạt hạng master trở lên để góp mặt!")
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
                                .targetRadius(60)).listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        compositeDisposable.add(apiHocTap.hoanThanhHuongDanMain_API(Utils.nguoiDungCurrent.getManguoidung())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        hoanthanhModel -> {
                                            if (hoanthanhModel.isSuccess()) {
                                                StyleableToast.makeText(getApplicationContext(), hoanthanhModel.getMessage(), R.style.success_toast).show();
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
    protected void onResume() {
        super.onResume();
        reloadDuLieu();
        initNavigation();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
        // Dừng dịch vụ phát nhạc nền
        Intent musicIntent = new Intent(this, MusicService.class);
        stopService(musicIntent);
    }


}