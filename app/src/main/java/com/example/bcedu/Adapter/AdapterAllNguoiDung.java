package com.example.bcedu.Adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bcedu.Dialog.DialogConfirmBan;
import com.example.bcedu.Dialog.DialogConfirmDelete;
import com.example.bcedu.Interface.DialogConfirmBanListener;
import com.example.bcedu.Interface.DialogConfirmDeleteListener;
import com.example.bcedu.Interface.ItemClickListenner;
import com.example.bcedu.Model.NguoiDung;
import com.example.bcedu.Model.XepHang;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AdapterAllNguoiDung extends RecyclerView.Adapter<AdapterAllNguoiDung.MyViewHolderAllND> {
    private List<NguoiDung> array;
    private List<NguoiDung> filteredArray;
    private Context context;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiHocTap apiHocTap;

    public AdapterAllNguoiDung(List<NguoiDung> array, Context context) {
        this.array = array;
        this.context = context;
        this.filteredArray = new ArrayList<>(array);
    }

    @NonNull
    @Override
    public MyViewHolderAllND onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nguoi_dung_admin, parent, false);
        return new MyViewHolderAllND(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderAllND holder, int position) {
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        // Lấy đối tượng người dùng hiện tại từ danh sách
        NguoiDung nguoiDung = filteredArray.get(position);

        holder.tenNguoiDung.setText(nguoiDung.getTennguoidung().toString().trim());
        Glide.with(context).load(Utils.BASE_URL + "images/" + nguoiDung.getAnhdaidien()).into(holder.hinhAnh);

        // Cập nhật drawable cho nút ban/unban
        updateBanDrawable(holder, nguoiDung.getXacthuc());

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

        holder.ban.setOnClickListener(v -> {
            DialogConfirmBan dialog = new DialogConfirmBan(context, new DialogConfirmBanListener() {
                @Override
                public void onBanConfirmed() {
                    if (nguoiDung.getXacthuc() == 0) {
                        banNguoiDung(holder, nguoiDung);
                    } else {
                        unbanNguoiDung(holder, nguoiDung);
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
            dialog.show();
        });

        holder.setItemClickListenner(new ItemClickListenner() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (nguoiDung.getXacthuc() == 0) {
                    StyleableToast.makeText(context, "Tài khoản: " + nguoiDung.getTennguoidung() + "\nTrạng thái: Hoạt động bình thường!", R.style.thongBaoDiem).show();
                } else {
                    StyleableToast.makeText(context, "Tài khoản: " + nguoiDung.getTennguoidung() + "\nTrạng thái: Đã bị cấm hoạt động!", R.style.thongBaoDiem).show();
                }
            }
        });
    }

    private void updateBanDrawable(MyViewHolderAllND holder, int xacthuc) {
        if (xacthuc == 0) {
            holder.ban.setImageResource(R.drawable.baseline_lock_open_24); // Drawable cho trạng thái chưa bị ban
        } else {
            holder.ban.setImageResource(R.drawable.baseline_lock_person_24  ); // Drawable cho trạng thái đã bị ban
        }
    }

    private void banNguoiDung(MyViewHolderAllND holder, NguoiDung nguoiDung) {
        compositeDisposable.add(apiHocTap.banNguoiDung_API(nguoiDung.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        banNguoiDungModel -> {
                            if (banNguoiDungModel.isSuccess()) {
                                nguoiDung.setXacthuc(1); // Cập nhật trạng thái người dùng
                                updateBanDrawable(holder, 1); // Cập nhật drawable
                                StyleableToast.makeText(context, banNguoiDungModel.getMessage(), R.style.success_toast).show();
                            } else {
                                StyleableToast.makeText(context, banNguoiDungModel.getMessage(), R.style.error_toast).show();
                            }
                        }, throwable -> {
                            StyleableToast.makeText(context, "Lỗi: " + throwable.getMessage(), R.style.error_toast).show();
                            Log.e("Loi", "BanNguoiDung: " + throwable.getMessage());
                        }
                ));
    }

    private void unbanNguoiDung(MyViewHolderAllND holder, NguoiDung nguoiDung) {
        compositeDisposable.add(apiHocTap.unbanNguoiDung_API(nguoiDung.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        banNguoiDungModel -> {
                            if (banNguoiDungModel.isSuccess()) {
                                nguoiDung.setXacthuc(0); // Cập nhật trạng thái người dùng
                                updateBanDrawable(holder, 0); // Cập nhật drawable
                                StyleableToast.makeText(context, banNguoiDungModel.getMessage(), R.style.success_toast).show();
                            } else {
                                StyleableToast.makeText(context, banNguoiDungModel.getMessage(), R.style.error_toast).show();
                            }
                        }, throwable -> {
                            StyleableToast.makeText(context, "Lỗi: " + throwable.getMessage(), R.style.error_toast).show();
                            Log.e("Loi", "UnbanNguoiDung: " + throwable.getMessage());
                        }
                ));
    }

    // Phương thức tìm kiếm
    public void filter(String text) {
        filteredArray.clear();
        if (text.isEmpty()) {
            filteredArray.addAll(array);
        } else {
            text = text.toLowerCase();
            for (NguoiDung item : array) {
                if (item.getTennguoidung().toLowerCase().contains(text)) {
                    filteredArray.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return filteredArray.size();
    }

    public class MyViewHolderAllND extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tenNguoiDung, tenRank;
        private ShapeableImageView hinhAnh, khungRank;
        private ItemClickListenner itemClickListenner;
        private ImageButton ban;

        public MyViewHolderAllND(@NonNull View itemView) {
            super(itemView);

            tenNguoiDung = itemView.findViewById(R.id.tenNguoiDung_admin);
            tenRank = itemView.findViewById(R.id.tenrank_admin);
            hinhAnh = itemView.findViewById(R.id.anhNguoiDung_admin);
            khungRank = itemView.findViewById(R.id.anhRankNguoiDung_admin);
            ban = itemView.findViewById(R.id.banNguoiDung_admin);
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
