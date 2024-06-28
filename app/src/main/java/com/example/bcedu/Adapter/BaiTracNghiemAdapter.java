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

import com.example.bcedu.Activities.KiemTra.SuaCauTracNghiem;
import com.example.bcedu.Dialog.DialogConfirmDelete;
import com.example.bcedu.Interface.DialogConfirmDeleteListener;
import com.example.bcedu.Interface.ItemClickListenner;
import com.example.bcedu.Model.CauTracNghiem;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;

import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BaiTracNghiemAdapter extends RecyclerView.Adapter<BaiTracNghiemAdapter.MyViewHolderTN> {
    private List<CauTracNghiem> array;
    private Context context;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
   private ApiHocTap apiHocTap;

    public BaiTracNghiemAdapter(List<CauTracNghiem> array, Context context) {
        this.array = array;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolderTN onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cau_trac_nghiem, parent, false);
        return new MyViewHolderTN(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderTN holder, int position) {
        MyViewHolderTN myViewHolder = holder;
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);

        CauTracNghiem cauTracNghiem = array.get(position);

        holder.txtCauHoi.setText(cauTracNghiem.getCauhoi().toString().trim());
        holder.txtDapAnA.setText("A. " + cauTracNghiem.getDapana());
        holder.txtDapAnB.setText("B. " + cauTracNghiem.getDapanb());
        holder.txtDapAnC.setText("C. " + cauTracNghiem.getDapanc());
        holder.txtDapAnD.setText("D. " + cauTracNghiem.getDapand());
        holder.txtDapAnDung.setText("Đáp án đúng: " + cauTracNghiem.getDapandung());

        myViewHolder.xoa.setOnClickListener(v -> {
            DialogConfirmDelete dialog = new DialogConfirmDelete(context, new DialogConfirmDeleteListener() {
                @Override
                public void onDeleteConfirmed() {
                    compositeDisposable.add(apiHocTap.xoaCauHoiTracNghiemAPI(cauTracNghiem.getMacautracnghiem())
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

        myViewHolder.setItemClickListenner(new ItemClickListenner() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent intent = new Intent(context, SuaCauTracNghiem.class);
                intent.putExtra("CauTracNghiem69", cauTracNghiem);
                context.startActivity(intent);
            }
        });

        myViewHolder.sua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SuaCauTracNghiem.class);
                intent.putExtra("CauTracNghiem69", cauTracNghiem);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class MyViewHolderTN extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txtCauHoi, txtDapAnA, txtDapAnB, txtDapAnC, txtDapAnD, txtDapAnDung;
        private ImageButton xoa, sua;
        private ItemClickListenner itemClickListenner;
        public MyViewHolderTN(@NonNull View itemView) {
            super(itemView);

            txtCauHoi = itemView.findViewById(R.id.textCauhoi);
            txtDapAnA = itemView.findViewById(R.id.txtDapAnA);
            txtDapAnB = itemView.findViewById(R.id.txtDapAnB);
            txtDapAnC = itemView.findViewById(R.id.txtDapAnC);
            txtDapAnD = itemView.findViewById(R.id.txtDapAnD);
            txtDapAnDung = itemView.findViewById(R.id.txtDapAnDung);

            xoa = itemView.findViewById(R.id.xoaTn);
            sua = itemView.findViewById(R.id.editTn);

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
