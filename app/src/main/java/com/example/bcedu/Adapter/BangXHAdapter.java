package com.example.bcedu.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bcedu.Interface.ItemClickListenner;
import com.example.bcedu.Model.NguoiDung;
import com.example.bcedu.Model.XepHang;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BangXHAdapter extends RecyclerView.Adapter<BangXHAdapter.MyViewHolderBXH>{
    private List<NguoiDung> array;
    private Context context;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiHocTap apiHocTap;
    public BangXHAdapter(List<NguoiDung> array, Context context) {
        this.array = array;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolderBXH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nguoi_dung_bxh, parent, false);
        return new MyViewHolderBXH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderBXH holder, int position) {
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        // Lấy đối tượng người dùng hiện tại từ danh sách
        NguoiDung nguoiDung = array.get(position);

        holder.tenNguoiDung.setText(nguoiDung.getTennguoidung().toString().trim());
        Glide.with(context).load(Utils.BASE_URL + "images/" + nguoiDung.getAnhdaidien()).into(holder.hinhAnh);

        compositeDisposable.add(apiHocTap.getTotalDiem_API(nguoiDung.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseModel -> {
                            if (responseModel.isSuccess()) {
                                holder.diem.setText(responseModel.getTotal_diem() + " điểm");
                            } else {
                                Log.d("Lỗi:", "lỗi lấy tổng điểm");
                            }
                        }, throwable -> {
                            Log.e("Loi", "DangKi: " + throwable.getMessage());
                        }
                ));

        // Lấy thông tin xếp hạng
        compositeDisposable.add(apiHocTap.getXepHangAPI(nguoiDung.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model -> {
                            if (model.isSuccess()) {
                                XepHang xepHang = model.getResult().get(0);
                                String tenBacRankString = xepHang.getTenxephang();
                                String khungRankString = xepHang.getHinhanh();
                                // Cập nhật lại giao diện
                                holder.tenRank.setText(tenBacRankString);
                                Glide.with(context).load(khungRankString).into(holder.khungRank);
                            } else {
                                Log.e("Loi", "Không thể lấy thông tin xếp hạng");
                            }
                        },
                        throwable -> {
                            Log.e("Loi", "get_xephang: " + throwable.getMessage());
                        }
                ));
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class MyViewHolderBXH extends RecyclerView.ViewHolder {
        private TextView tenNguoiDung, tenRank, diem;
        private ShapeableImageView hinhAnh, khungRank;

        public MyViewHolderBXH(@NonNull View itemView) {
            super(itemView);

            tenNguoiDung = itemView.findViewById(R.id.tenNguoiDung_bxh);
            tenRank = itemView.findViewById(R.id.tenrank_bxh);
            hinhAnh = itemView.findViewById(R.id.anhNguoiDung_bxh);
            khungRank = itemView.findViewById(R.id.anhRankNguoiDung_bxh);
        }
    }
}
