package com.example.bcedu.Activities.TuVung;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.bcedu.Activities.Admin.AdminMain;
import com.example.bcedu.Activities.KhoaHoc.KhoaHocActivity;
import com.example.bcedu.Activities.KhoaHoc.QuanLyKhoaHoc;
import com.example.bcedu.Activities.KhoaHoc.QuanLyKhoaHocDaThamGia;
import com.example.bcedu.Activities.MainActivity;
import com.example.bcedu.Activities.NguoiDung.SignIn;
import com.example.bcedu.Activities.NguoiDung.ThongTinCaNhan;
import com.example.bcedu.Activities.Setting;
import com.example.bcedu.Activities.XepHangAll;
import com.example.bcedu.Adapter.CapDoTuVungAdapter;
import com.example.bcedu.Dialog.DialogAppLoading;
import com.example.bcedu.Model.KetQuaOnTap;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CapDoTuVung extends AppCompatActivity {
    private ListView listViewCapDoTuVung;
    private List<com.example.bcedu.Model.CapDoTuVung> mangCapDoTuVung;
    private CapDoTuVungAdapter capDoTuVungAdapter;
    private ImageButton reloadBtn, back;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiHocTap apiHocTap;
    private DialogAppLoading dialogAppLoading;
    private Toolbar toolbar;
    private NavigationView navigation;
    private DrawerLayout drawerLayout;
    private ImageButton toolbarBtn;
    private TextView tenNguoiDung, vaiTroNav, rank, text;
    private ShapeableImageView avt, rankFrame;
    private boolean isButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initGiaoDien();
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        // ánh xa các thành phần UI
        anhXa();

        //hiển thi loading animation
        dialogAppLoading.show();

        // init Navigation
        initNavigation();

        if (Utils.isConnected(this)) {
            getCDTV();
        } else {
            StyleableToast.makeText(this, "Không có Internet!", R.style.error_toast).show();
        }

        // lắng nghe sự kiện khi click vào  item của listview cấp độ từ vựng, nút reload, nút back
        eventClickListenner();

        //Hiênr thị hướng dẫn nếu người dùng chưa được hướng dẫn
        if (Utils.nguoiDungCurrent.getHdtuvung() == 0) {
            huongDanSuDungCDTV();
        }
    }

    private void huongDanSuDungCDTV() {
        dialogAppLoading.cancel();

        TapTargetView.showFor(this,
                TapTarget.forView(reloadBtn, "CẤP ĐỘ TỪ VỰNG", "Đây là những thử thách lớn mà bạn cần vượt qua!\n" +
                                "Chú ý:\n" +
                                "1. Bạn chỉ có thể học cấp độ tiếp theo nếu cấp độ trước đạt được 70 điểm trở lên!\n" +
                                "2. Bạn sẽ chỉ đạt được mức rank mới chỉ khi ở cấp độ đó đạt được 100 điểm")
                        .outerCircleColor(R.color.xanhduong)
                        .outerCircleAlpha(0.96f)
                        .targetCircleColor(R.color.white)
                        .titleTextSize(20)
                        .titleTextColor(R.color.white)
                        .descriptionTextSize(15)
                        .descriptionTextColor(R.color.white)
                        .textColor(R.color.white)
                        .textTypeface(Typeface.DEFAULT)
                        .dimColor(R.color.black)
                        .drawShadow(true)
                        .cancelable(false)
                        .tintTarget(true)
                        .transparentTarget(false)
                        .icon(ContextCompat.getDrawable(this, R.drawable.baseline_lightbulb_24))
                        .targetRadius(60),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        compositeDisposable.add(apiHocTap.hoanThanhHuongDanCDTV_API(Utils.nguoiDungCurrent.getManguoidung())
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
                });
    }

    private void eventClickListenner() {
        listViewCapDoTuVung.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isButtonClicked) {
                    // Nếu nút đã được nhấp, không làm gì cả
                    return;
                }

                isButtonClicked = true;

                // Disable the button
                view.setEnabled(false);

                if (position > 0) {
                    int macapdo = position - 1;

                    // Lấy điểm cao nhất của CapDoTuVung đó
                    compositeDisposable.add(apiHocTap.getKetQuaOnTapAPI(macapdo + 1, Utils.nguoiDungCurrent.getManguoidung())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    ketQuaOnTapModel -> {
                                        if (ketQuaOnTapModel.isSuccess()) {
                                            float diem;
                                            KetQuaOnTap ketQuaOnTap = ketQuaOnTapModel.getResult().get(0);
                                            diem = ketQuaOnTap.getDiemcaonhat();

                                            if (diem < 100 || diem == 0) {
                                                StyleableToast.makeText(getApplicationContext(), "Cần đạt được bậc xếp hạng trước khi học cấp độ mới!", R.style.warning).show();
                                                // Reset cờ và nút sau khi Toast hiển thị xong (sau 2 giây)
                                                new Handler().postDelayed(() -> {
                                                    isButtonClicked = false;
                                                    view.setEnabled(true);
                                                }, 2000);
                                            } else {
                                                Intent level = new Intent(CapDoTuVung.this, TuVungTheoCapDo.class);
                                                level.putExtra("macapdoIntent", position);
                                                level.putExtra("diemHienTai", diem);
                                                startActivity(level);
                                                // Reset cờ và nút ngay sau khi bắt đầu Activity mới
                                                isButtonClicked = false;
                                                view.setEnabled(true);
                                            }
                                        } else {
                                            // Reset cờ và nút nếu không thành công
                                            isButtonClicked = false;
                                            view.setEnabled(true);
                                        }
                                    },
                                    throwable -> {
                                        // Xử lý ngoại lệ ở đây
                                        Log.e("logCDTV", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                                        // Reset cờ và nút nếu có lỗi xảy ra
                                        isButtonClicked = false;
                                        view.setEnabled(true);
                                    }
                            ));
                } else {
                    Intent level = new Intent(CapDoTuVung.this, TuVungTheoCapDo.class);
                    level.putExtra("macapdoIntent", position);
                    startActivity(level);
                    // Reset cờ và nút ngay sau khi bắt đầu Activity mới
                    isButtonClicked = false;
                    view.setEnabled(true);
                }
            }
        });

        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCDTV();
                dialogAppLoading.show();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        dialogAppLoading.cancel();
                        StyleableToast.makeText(CapDoTuVung.this, "Đã cập nhật danh sách từ vựng thành công!", R.style.success_toast).show();
                    }
                };
                handler.postDelayed(runnable, 2000);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        text = headerView.findViewById(R.id.ttt);

        Glide.with(this).load(Utils.BASE_URL + "images/" + Utils.nguoiDungCurrent.getAnhdaidien()).into(avt);
        tenNguoiDung.setText(Utils.nguoiDungCurrent.getTennguoidung());
        vaiTroNav.setText(Utils.nguoiDungCurrent.getVaitro());

        rank.setText(Utils.xepHangCuaToi.getTenxephang());
        Glide.with(this).load(Utils.xepHangCuaToi.getHinhanh()).into(rankFrame);

        //navigation drawer
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.nav_home) {
                    Intent intentTuVung = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intentTuVung);
                    finish();
                }
                if (itemId == R.id.nav_admin) {
                    if (Utils.nguoiDungCurrent.getVaitro().equals("Admin")) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                        StyleableToast.makeText(getApplicationContext(), "Chào mừng Admin " + Utils.nguoiDungCurrent.getTennguoidung(), R.style.thongBaoDiem).show();
                        Intent intentAdmin = new Intent(getApplicationContext(), AdminMain.class);
                        startActivity(intentAdmin);
                    } else {
                        StyleableToast.makeText(getApplicationContext(), "Chỉ có Admin mới có quyền truy cập!", R.style.warning).show();
                    }
                }
                if (itemId == R.id.nav_hocTuVung) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (itemId == R.id.nav_thongTinTaiKhoan) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(getApplicationContext(), ThongTinCaNhan.class);
                    startActivity(intent);
                }
                if (itemId == R.id.nav_xepHang) {
                    drawerLayout.closeDrawer(GravityCompat.START);
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
                    finish();
                }
                if (itemId == R.id.nav_caidat) {
                    Intent intent = new Intent(getApplicationContext(), Setting.class);
                    startActivity(intent);
                }
                if (itemId == R.id.nav_khoaHocCuaToi) {
                    Intent intent = new Intent(getApplicationContext(), QuanLyKhoaHocDaThamGia.class);
                    startActivity(intent);
                }
                if (itemId == R.id.nav_khoaHocDaTao) {
                    if (Utils.nguoiDungCurrent.getVaitro().equals("Giảng Viên") || Utils.nguoiDungCurrent.getVaitro().equals("Admin")) {
                        StyleableToast.makeText(getApplicationContext(), "Chào mừng " + Utils.nguoiDungCurrent.getVaitro() + " " + Utils.nguoiDungCurrent.getTennguoidung(), R.style.thongBaoDiem).show();
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

    private void getCDTV() {
        compositeDisposable.add(apiHocTap.getCapDoTuVungAPI()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        capDoTuVungModel -> {
                            if (capDoTuVungModel.isSuccess()) {
                                dialogAppLoading.cancel();
                                mangCapDoTuVung = capDoTuVungModel.getResult();
                                capDoTuVungAdapter = new CapDoTuVungAdapter(mangCapDoTuVung, this);
                                listViewCapDoTuVung.setAdapter(capDoTuVungAdapter);
                            } else {
                                dialogAppLoading.cancel();
                                StyleableToast.makeText(this, "Chưa có cấp độ từ vựng nào!", R.style.warning).show();
                            }
                        },
                        throwable -> {
                            // Xử lý ngoại lệ ở đây
                            dialogAppLoading.cancel();
                            Log.e("logCDTV", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                        }
                ));
    }

    private void anhXa() {
        listViewCapDoTuVung = findViewById(R.id.listview_capdo_tuvung);
        mangCapDoTuVung = new ArrayList<>();

        reloadBtn = findViewById(R.id.reloadBtn);

        dialogAppLoading = new DialogAppLoading(this);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerMain);
        navigation = findViewById(R.id.navigationView);

        toolbarBtn = findViewById(R.id.toolbarBtn);
        back = findViewById(R.id.back_CDTV);
    }

    private void initGiaoDien() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hoc_tu_vung);
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

    @Override
    protected void onResume() {
        super.onResume();
        reloadDuLieu();
        initNavigation();
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
}