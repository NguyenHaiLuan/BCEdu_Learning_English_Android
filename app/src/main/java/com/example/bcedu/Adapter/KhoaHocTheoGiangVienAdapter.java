package com.example.bcedu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bcedu.Activities.KhoaHoc.ChinhSuaKhoaHoc;
import com.example.bcedu.Activities.KhoaHoc.TrangChuKhoaHoc;
import com.example.bcedu.Dialog.DialogConfirmDelete;
import com.example.bcedu.Interface.DialogConfirmDeleteListener;
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

public class KhoaHocTheoGiangVienAdapter extends RecyclerView.Adapter<KhoaHocTheoGiangVienAdapter.MyViewHolder_KHTGV> {
    private List<KhoaHoc> array;
    private Context context;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiHocTap apiHocTap;

    public KhoaHocTheoGiangVienAdapter(List<KhoaHoc> array, Context context) {
        this.array = array;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder_KHTGV onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_khoa_hoc_da_tao, parent, false);
        return new KhoaHocTheoGiangVienAdapter.MyViewHolder_KHTGV(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder_KHTGV holder, int position) {
        MyViewHolder_KHTGV myViewHolder = holder;

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        KhoaHoc khoaHoc = array.get(position);

        holder.tenKhoaHoc.setText(khoaHoc.getTenkhoahoc().trim());
        Glide.with(context).load(Utils.BASE_URL + "images/" +khoaHoc.getHinhanhmota()).into(holder.hinhAnh);

        holder.soLuongThanhVien.setText("");

        compositeDisposable.add(apiHocTap.getDanhSachThamGiaKhoaHocAPI(khoaHoc.getMakhoahoc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        danhSachThamGiaModel -> {
                            if (danhSachThamGiaModel.isSuccess()) {
                                List<NguoiDung> mangNguoiDung = danhSachThamGiaModel.getResult();
                                int soLuong = (mangNguoiDung != null) ? mangNguoiDung.size() : 0;


                                if (soLuong > 0){
                                    holder.soLuongThanhVien.setText(soLuong + " thành viên");
                                    holder.nothanhvien.setVisibility(View.INVISIBLE);
                                }

                            } else {
                                Log.d("LoiLoadDuLieuThanhVien", danhSachThamGiaModel.getMessage());
                            }
                        },
                        throwable -> {
                            // Xử lý ngoại lệ ở đây
                            Log.e("Error", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                        }
                ));

        myViewHolder.setItemClickListenner(new ItemClickListenner() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!isLongClick) {
                    Intent intent = new Intent(context, TrangChuKhoaHoc.class);

                    //Truyền mã xác nhận
                    intent.putExtra("maXacNhan", 69);
                    //Truyền từ vựng chi tiết sang mảng chi tiết từ vưng
                    intent.putExtra("chitietKhoahoc", khoaHoc);

                    Bundle bundle = new Bundle();
                    intent.putExtras(bundle);

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        myViewHolder.xoa.setOnClickListener(v -> {
            DialogConfirmDelete dialog = new DialogConfirmDelete(context, new DialogConfirmDeleteListener() {
                @Override
                public void onDeleteConfirmed() {
                    compositeDisposable.add(apiHocTap.xoaKhoaHocAPI(khoaHoc.getMakhoahoc())
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


        myViewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChinhSuaKhoaHoc.class);
                intent.putExtra("chitietKhoahocAdapter", khoaHoc);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class MyViewHolder_KHTGV extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tenKhoaHoc, soLuongThanhVien, nothanhvien;
        ImageView hinhAnh, xoa, edit;
        private ItemClickListenner itemClickListenner;

        public MyViewHolder_KHTGV(@NonNull View itemView) {
            super(itemView);

            tenKhoaHoc = itemView.findViewById(R.id.ten_khoa_hoc_da_tao);
            soLuongThanhVien = itemView.findViewById(R.id.soLuongHocVien);
            hinhAnh = itemView.findViewById(R.id.hinh_mota_khoa_hoc_da_tao);
            nothanhvien = itemView.findViewById(R.id.nothanhvien);

            xoa = itemView.findViewById(R.id.xoaKhoaHoc_DaTao);
            edit = itemView.findViewById(R.id.chinhSuaKhoaHoc_DaTao);

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
