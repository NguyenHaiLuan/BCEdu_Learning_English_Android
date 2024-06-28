package com.example.bcedu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bcedu.Activities.KhoaHoc.TrangChuKhoaHoc;
import com.example.bcedu.Dialog.DialogConfirmLeave;
import com.example.bcedu.Interface.DialogConfirmLeaveListener;
import com.example.bcedu.Interface.ItemClickListenner;
import com.example.bcedu.Model.KhoaHoc;
import com.example.bcedu.Model.NguoiDung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class KhoaHocTheoHocVienAdapter extends RecyclerView.Adapter<KhoaHocTheoHocVienAdapter.MyViewHolder_KHTHV> {
    private List<KhoaHoc> array;
    private Context context;
    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private NguoiDung nguoiDung;
    private String tenGiangVien;
    private int maGiangVien;

    public KhoaHocTheoHocVienAdapter(List<KhoaHoc> array, Context context) {
        this.array = array;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder_KHTHV onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_khoa_hoc_da_tham_gia, parent, false);
        return new MyViewHolder_KHTHV(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder_KHTHV holder, int position) {
        MyViewHolder_KHTHV myViewHolder = holder;

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        KhoaHoc khoaHoc = array.get(position);
        holder.tenKhoaHoc.setText(khoaHoc.getTenkhoahoc().trim());
        Glide.with(context).load(Utils.BASE_URL + "images/" +khoaHoc.getHinhanhmota()).into(holder.hinhAnh);

        // lấy thông tin của giảng viên
        compositeDisposable.add(apiHocTap.getGiangVien_API(array.get(position).getMakhoahoc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        model -> {
                            if (model.isSuccess()) {
                                nguoiDung = model.getResult().get(0);
                                tenGiangVien = nguoiDung.getTennguoidung();
                                maGiangVien = nguoiDung.getManguoidung();

                                holder.tenGV.setText("Giảng viên: " + tenGiangVien);
                            }
                        },
                        throwable -> {
                            Log.e("Loi", "get_xephang: " + throwable.getMessage());
                        }
                ));

        myViewHolder.setItemClickListenner(new ItemClickListenner() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!isLongClick) {
                    Intent intent = new Intent(context, TrangChuKhoaHoc.class);

                    //Truyền mã xác nhận
                    intent.putExtra("maXacNhan", 45);
                    //Truyền từ vựng chi tiết sang mảng chi tiết từ vưng
                    intent.putExtra("chitietKhoahoc45", khoaHoc);

                    Bundle bundle = new Bundle();
                    intent.putExtras(bundle);

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        myViewHolder.roiKhoiBtn.setOnClickListener(v -> {
            DialogConfirmLeave dialog = new DialogConfirmLeave(context, new DialogConfirmLeaveListener() {
                @Override
                public void onLeaveConfirmed() {
                    compositeDisposable.add(apiHocTap.roiKhoaHocApi(khoaHoc.getMakhoahoc(), Utils.nguoiDungCurrent.getManguoidung())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    roiKhoaHocModel -> {
                                        if (roiKhoaHocModel.isSuccess()) {
                                            StyleableToast.makeText(context, roiKhoaHocModel.getMessage(), R.style.success_toast).show();

                                            // Xóa mục khỏi danh sách và cập nhật RecyclerView
                                            int position = myViewHolder.getAdapterPosition();
                                            array.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, array.size());

                                        } else {
                                            StyleableToast.makeText(context, roiKhoaHocModel.getMessage(), R.style.error_toast).show();
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
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class MyViewHolder_KHTHV extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tenKhoaHoc, tenGV;
        private ImageView hinhAnh, roiKhoiBtn;

        private ItemClickListenner itemClickListenner;

        public MyViewHolder_KHTHV(@NonNull View itemView) {
            super(itemView);

            tenKhoaHoc = itemView.findViewById(R.id.ten_khoa_hoc_da_tham_gia);
            hinhAnh = itemView.findViewById(R.id.hinh_mota_khoa_hoc_da_tham_gia);
            tenGV = itemView.findViewById(R.id.giaovienkhoahoc_da_tham_gia);

            roiKhoiBtn = itemView.findViewById(R.id.roiKhoiNhomBtn);

            itemView.setOnClickListener(this);


        }

        public void setItemClickListenner(ItemClickListenner itemClickListenner) {
            this.itemClickListenner = itemClickListenner;
        }

        @Override
        public void onClick(View v) {
            itemClickListenner.onClick(v, getAdapterPosition(), false);
        }
    }
}
