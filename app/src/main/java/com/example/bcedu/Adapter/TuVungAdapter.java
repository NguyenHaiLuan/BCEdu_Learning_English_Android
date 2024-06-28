package com.example.bcedu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bcedu.Activities.TuVung.ChiTietTuVung;
import com.example.bcedu.Interface.ItemClickListenner;
import com.example.bcedu.Model.TuVung;
import com.example.bcedu.R;

import java.io.Serializable;
import java.util.List;

public class TuVungAdapter extends RecyclerView.Adapter<TuVungAdapter.MyViewHolder> {
    private List<TuVung> array;
    private Context context;

    public TuVungAdapter(List<TuVung> array, Context context) {
        this.array = array;
        this.context = context;
    }

    @NonNull
    @Override
    public TuVungAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tu_vung, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TuVungAdapter.MyViewHolder holder, int position) {
        MyViewHolder myViewHolder = holder;

        TuVung tuVung = array.get(position);
        holder.tuVung.setText(tuVung.getTentuvung().trim());
        holder.nghia.setText(tuVung.getYnghia().trim());
        holder.phatAm.setText("/"+tuVung.getPhatam().trim()+"/");
        Glide.with(context).load(tuVung.getHinhanhmota()).into(holder.hinhAnh);

        myViewHolder.setItemClickListenner(new ItemClickListenner() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!isLongClick){
                    Intent intent = new Intent(context, ChiTietTuVung.class);

                    //Truyền từ vựng chi tiết sang mảng chi tiết từ vưng
                    intent.putExtra("chitiet",tuVung);

                    // Truyền file audio của từ vựng đó sang Ôn tập từ vựng
                    intent.putExtra("audio", tuVung.getAudio());

                    // Truyền danh sách từ vựng sang Chi tiết từ vựng
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("danhSachTuVung", (Serializable) array);
                    intent.putExtras(bundle);

                    intent.putExtra("position", position);

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tuVung, phatAm, nghia, level;
        ImageView hinhAnh;
        private ItemClickListenner itemClickListenner;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tuVung = itemView.findViewById(R.id.tenTuVung);
            nghia = itemView.findViewById(R.id.nghiaTuVung);
            phatAm = itemView.findViewById(R.id.phienAmTuVung);
            level = itemView.findViewById(R.id.textViewLevel);

            hinhAnh = itemView.findViewById(R.id.hinh_mota_tuvung);

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
