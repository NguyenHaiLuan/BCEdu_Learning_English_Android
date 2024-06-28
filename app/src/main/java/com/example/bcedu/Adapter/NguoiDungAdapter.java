package com.example.bcedu.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bcedu.Model.NguoiDung;
import com.example.bcedu.Model.XepHang;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NguoiDungAdapter extends BaseAdapter {
    private List<NguoiDung> array;
    private Context context;
    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String tenBacRankString;
    private String khungRankString;

    XepHang xepHang = new XepHang();

    public NguoiDungAdapter(List<NguoiDung> array, Context context) {
        this.array = array;
        this.context = context;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class MyViewHolderND{
        private TextView tenNguoiDung, tenRank;
        private ShapeableImageView hinhAnh, khungRank;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MyViewHolderND viewHolder;
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        if (view == null) {
            viewHolder = new MyViewHolderND();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_hocvien_chitiet_khoahoc, null);
            viewHolder.tenNguoiDung = view.findViewById(R.id.tenHocVien);
            viewHolder.hinhAnh = view.findViewById(R.id.anhHocVien);
            viewHolder.tenRank = view.findViewById(R.id.tenRankHocVienCT);
            viewHolder.khungRank = view.findViewById(R.id.anhRankHocVienCT);
            view.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolderND) view.getTag();
        }

        // Thiết lập thông tin người dùng cơ bản
        viewHolder.tenNguoiDung.setText(array.get(position).getTennguoidung());
        Glide.with(context).load(Utils.BASE_URL + "images/" +array.get(position).getAnhdaidien()).into(viewHolder.hinhAnh);

        // Đặt giá trị mặc định để tránh lỗi nếu không có dữ liệu
        viewHolder.tenRank.setText("");
        viewHolder.khungRank.setImageDrawable(null); // Hoặc một hình ảnh mặc định

        // Lấy thông tin xếp hạng
        compositeDisposable.add(apiHocTap.getXepHangAPI(array.get(position).getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model -> {
                            if (model.isSuccess() && viewHolder != null) {
                                xepHang = model.getResult().get(0);
                                tenBacRankString = xepHang.getTenxephang();
                                khungRankString = xepHang.getHinhanh();
                                // Cập nhật lại giao diện
                                viewHolder.tenRank.setText(tenBacRankString);
                                Glide.with(context).load(khungRankString).into(viewHolder.khungRank);
                            } else {
                                Log.e("Loi", "Không thể lấy thông tin xếp hạng");
                            }
                        },
                        throwable -> {
                            Log.e("Loi", "get_xephang: " + throwable.getMessage());
                        }
                ));
        return view;
    }

}
