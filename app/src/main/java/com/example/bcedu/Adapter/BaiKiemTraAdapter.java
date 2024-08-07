package com.example.bcedu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bcedu.Activities.KiemTra.ChinhSuaBaiKiemTra;
import com.example.bcedu.Activities.KiemTra.GioiThieuBaiKiemTra;
import com.example.bcedu.Dialog.DialogConfirmDelete;
import com.example.bcedu.Interface.DialogConfirmDeleteListener;
import com.example.bcedu.Interface.ItemClickListenner;
import com.example.bcedu.Model.BaiKiemTra;
import com.example.bcedu.Model.NguoiDung;
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

public class BaiKiemTraAdapter extends RecyclerView.Adapter<BaiKiemTraAdapter.MyViewHolderBaiKiemTtra> {
    private List<BaiKiemTra> array;
    private Context context;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiHocTap apiHocTap;
    private NguoiDung nguoiDung = new NguoiDung();

    public BaiKiemTraAdapter(List<BaiKiemTra> array, Context context) {
        this.array = array;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolderBaiKiemTtra onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bai_kiem_tra, parent, false);
        return new MyViewHolderBaiKiemTtra(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderBaiKiemTtra holder, int position) {
        MyViewHolderBaiKiemTtra myViewHolder = holder;

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        BaiKiemTra baiKiemTra = array.get(position);

        holder.tenBaiKiemTra.setText(baiKiemTra.getTenbaikiemtra().toString().trim());
        holder.thoiLuong.setText("Thời lượng: "+baiKiemTra.getThoiluong() + " phút");

        holder.soLuongCauHoi.setText("Số câu hỏi: " + baiKiemTra.getSocauhoi());

        Glide.with(context).load(Utils.BASE_URL + "images/" +baiKiemTra.getHinhanhminhhoa().toString().trim()).into(holder.hinhBaiKiemTra);

        // lấy thông tin của giảng viên
        compositeDisposable.add(apiHocTap.getGiangVien_API(baiKiemTra.getMakhoahoc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model -> {
                            if (model.isSuccess()) {
                                nguoiDung = model.getResult().get(0);
                                int maGiangVien = nguoiDung.getManguoidung();
                                if (Utils.nguoiDungCurrent.getManguoidung() == maGiangVien){
                                    holder.khoiXoa.setVisibility(View.VISIBLE);
                                    holder.khoiSua.setVisibility(View.VISIBLE);
                                } else {
                                    holder.khoiTT.setVisibility(View.VISIBLE);
                                }
                            }
                        },
                        throwable -> {
                            Log.e("Loi", "get_xephang: " + throwable.getMessage());
                        }
                ));

        myViewHolder.setItemClickListenner(new ItemClickListenner() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(context, GioiThieuBaiKiemTra.class);
                // truyền bài kiểm tra sang để giới thiệu thông tin bài kiểm tra
                intent.putExtra("chiTietBKT", baiKiemTra);
                context.startActivity(intent);
            }
        });

        myViewHolder.xoa.setOnClickListener(v -> {
            DialogConfirmDelete dialog = new DialogConfirmDelete(context, new DialogConfirmDeleteListener() {
                @Override
                public void onDeleteConfirmed() {
                    compositeDisposable.add(apiHocTap.xoaBaiKiemTraAPI(baiKiemTra.getMabaikiemtra())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    xoaKhoaHocModel -> {
                                        if (xoaKhoaHocModel.isSuccess()) {
                                            StyleableToast.makeText(context, xoaKhoaHocModel.getMessage(), R.style.success_toast).show();

                                            // Xóa mục khỏi danh sách và cập nhật RecyclerView
                                            int position = myViewHolder.getAdapterPosition();
                                            array.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, array.size());

                                        } else {
                                            StyleableToast.makeText(context, xoaKhoaHocModel.getMessage(), R.style.error_toast).show();
                                        }
                                    }, throwable -> {
                                        StyleableToast.makeText(context, "Lỗi: " + throwable.getMessage(), R.style.error_toast).show();
                                        Log.e("Loi", "DangKi: " + throwable.getMessage());
                                    }
                            ));
                }
            });
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
            dialog.show();
        });

        myViewHolder.sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChinhSuaBaiKiemTra.class);
                intent.putExtra("baiKiemTraAdapter", baiKiemTra);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class MyViewHolderBaiKiemTtra extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView tenBaiKiemTra, soLuongCauHoi, thoiLuong;
        private ShapeableImageView hinhBaiKiemTra;
        private ImageView sua, xoa, nutTrangTri;
        private RelativeLayout khoiSua, khoiXoa, khoiTT;
        private ItemClickListenner itemClickListenner;

        public MyViewHolderBaiKiemTtra(@NonNull View itemView) {
            super(itemView);

            tenBaiKiemTra = itemView.findViewById(R.id.tenBaiKiemTra);
            soLuongCauHoi = itemView.findViewById(R.id.soCauHoiBaiKT);
            thoiLuong = itemView.findViewById(R.id.thoiLuongBaiKiemTra);
            hinhBaiKiemTra = itemView.findViewById(R.id.hinh_bai_kiem_tra);

            sua = itemView.findViewById(R.id.chinhSuaBaiKT);
            xoa = itemView.findViewById(R.id.xoaBaiKT);
            nutTrangTri = itemView.findViewById(R.id.nutTrangTri_BKT);

            khoiSua = itemView.findViewById(R.id.khoiChinhSua);
            khoiXoa = itemView.findViewById(R.id.khoiXoa);
            khoiTT = itemView.findViewById(R.id.khoiTrangTri);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListenner.onClick(v, getAdapterPosition(), false);
        }

        public void setItemClickListenner(ItemClickListenner itemClickListenner) {
            this.itemClickListenner = itemClickListenner;
        }
    }
}
