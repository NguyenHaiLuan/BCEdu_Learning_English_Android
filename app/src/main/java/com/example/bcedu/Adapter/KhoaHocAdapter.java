package com.example.bcedu.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bcedu.Model.KhoaHoc;
import com.example.bcedu.Model.NguoiDung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class KhoaHocAdapter extends BaseAdapter {
    private List<KhoaHoc> array;
    private List<KhoaHoc> filteredArray;
    private Context context;
    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private NguoiDung nguoiDung = new NguoiDung();
    private String tenGiangVien;
    private int maGiangVien;

    public KhoaHocAdapter(List<KhoaHoc> array, Context context) {
        this.array = array;
        this.filteredArray = new ArrayList<>(array); // Khởi tạo filteredArray
        this.context = context;
    }

    @Override
    public int getCount() {
        return filteredArray.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        private TextView tenKhoaHoc, giangVien, toi, tagDaThamGia;
        private ShapeableImageView hinhAnh;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder;
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_khoa_hoc, null);
            viewHolder.tenKhoaHoc = view.findViewById(R.id.ten_khoa_hoc_txt);
            viewHolder.hinhAnh = view.findViewById(R.id.hinh_mota_khoa);
            viewHolder.giangVien = view.findViewById(R.id.giaovienkhoahoc);
            viewHolder.toi = view.findViewById(R.id.giaovienkhoahocToiTao);
            viewHolder.tagDaThamGia = view.findViewById(R.id.daThamGiaTag_KH);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            compositeDisposable.clear();
        }

        // Đặt lại trạng thái mặc định cho các thành phần UI
        viewHolder.tagDaThamGia.setVisibility(View.INVISIBLE);
        viewHolder.toi.setVisibility(View.INVISIBLE);
        viewHolder.giangVien.setText("");

        KhoaHoc khoaHoc = filteredArray.get(position);
        viewHolder.tenKhoaHoc.setText(khoaHoc.getTenkhoahoc());
        Glide.with(context).load(Utils.BASE_URL + "images/" + khoaHoc.getHinhanhmota()).into(viewHolder.hinhAnh);

        // Lấy thông tin của giảng viên khoas học đó
        compositeDisposable.add(apiHocTap.getGiangVien_API(khoaHoc.getMakhoahoc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model -> {
                            if (model.isSuccess() && viewHolder != null) {
                                nguoiDung = model.getResult().get(0);
                                tenGiangVien = nguoiDung.getTennguoidung();
                                maGiangVien = nguoiDung.getManguoidung();

                                if (maGiangVien == Utils.nguoiDungCurrent.getManguoidung()) {
                                    viewHolder.toi.setVisibility(View.VISIBLE);
                                    viewHolder.giangVien.setVisibility(View.INVISIBLE);
                                } else {
                                    viewHolder.giangVien.setText("Giảng viên: " + tenGiangVien);
                                }
                            } else {
                                Log.e("Loi", "Không thể lấy thông tin xếp hạng");
                            }
                        },
                        throwable -> {
                            Log.e("Loi", "get_xephang: " + throwable.getMessage());
                        }
                ));

        compositeDisposable.add(apiHocTap.getDanhSachThamGiaKhoaHocAPI(khoaHoc.getMakhoahoc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        danhSachThamGiaModel -> {
                            if (danhSachThamGiaModel.isSuccess()) {
                                List<NguoiDung> danhSachThamGia = danhSachThamGiaModel.getResult();
                                boolean isCurrentUserInList = false;

                                // Kiểm tra xem mã người dùng hiện tại có trong danh sách tham gia không
                                for (NguoiDung nguoiDung : danhSachThamGia) {
                                    if (nguoiDung.getManguoidung() == Utils.nguoiDungCurrent.getManguoidung()) {
                                        isCurrentUserInList = true;
                                        break;
                                    }
                                }

                                if (isCurrentUserInList) {
                                    viewHolder.tagDaThamGia.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Log.e("Loi", "Không thể lấy danh sách tham gia");
                            }
                        },
                        throwable -> {
                            Log.e("Error", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                        }
                ));

        return view;
    }

    public void filter(String query) {
        filteredArray.clear();
        if (query.isEmpty()) {
            filteredArray.addAll(array);
        } else {
            for (KhoaHoc khoaHoc : array) {
                if (khoaHoc.getTenkhoahoc().toLowerCase().contains(query.toLowerCase()) || tenGiangVien.toLowerCase().contains(query.toLowerCase()) ){
                    filteredArray.add(khoaHoc);
                }
            }
        }
        notifyDataSetChanged();
    }
}

