package com.example.bcedu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bcedu.Activities.Admin.Admin_SuaTuVung;
import com.example.bcedu.Activities.Admin.Admin_ThemTuVung;
import com.example.bcedu.Dialog.DialogConfirmDelete;
import com.example.bcedu.Interface.DialogConfirmDeleteListener;
import com.example.bcedu.Interface.ItemClickListenner;
import com.example.bcedu.Model.KhoaHoc;
import com.example.bcedu.Model.TuVung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TuVungAdminAdapter extends RecyclerView.Adapter<TuVungAdminAdapter.MyViewHolderTV_ADMIN> {
    private List<TuVung> array;
    private Context context;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiHocTap apiHocTap;
    public TuVungAdminAdapter(List<TuVung> array, Context context) {
        this.array = array;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolderTV_ADMIN onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tu_vung_admin, parent, false);
        return new MyViewHolderTV_ADMIN(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderTV_ADMIN holder, int position) {
        MyViewHolderTV_ADMIN myViewHolder = holder;

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        TuVung tuVung = array.get(position);

        holder.tenTuVung.setText(tuVung.getTentuvung().toString().trim());
        holder.tuLoai.setText(tuVung.getYnghia().toString().trim());

        myViewHolder.setItemClickListenner(new ItemClickListenner() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!isLongClick){
                    Intent intent = new Intent(context, Admin_SuaTuVung.class);
                    intent.putExtra("adapter_tuvung_admin_tuvung", tuVung);
                    context.startActivity(intent);
                }
            }
        });

        myViewHolder.sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Admin_SuaTuVung.class);
                intent.putExtra("adapter_tuvung_admin_tuvung", tuVung);
                context.startActivity(intent);
            }
        });

        myViewHolder.xoa.setOnClickListener(v -> {
            DialogConfirmDelete dialog = new DialogConfirmDelete(context, new DialogConfirmDeleteListener() {
                @Override
                public void onDeleteConfirmed() {
                    compositeDisposable.add(apiHocTap.xoaTuVungAPI(tuVung.getMatuvung())
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
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class MyViewHolderTV_ADMIN extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tenTuVung, tuLoai;
        private ImageButton sua, xoa;
        private ItemClickListenner itemClickListenner;

        public MyViewHolderTV_ADMIN(@NonNull View itemView) {
            super(itemView);

            tenTuVung = itemView.findViewById(R.id.tuVungAdmin);
            tuLoai = itemView.findViewById(R.id.tuLoaiAdmin);
            sua = itemView.findViewById(R.id.suaTuVung_admin);
            xoa = itemView.findViewById(R.id.xoaTuVung_admin);

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
