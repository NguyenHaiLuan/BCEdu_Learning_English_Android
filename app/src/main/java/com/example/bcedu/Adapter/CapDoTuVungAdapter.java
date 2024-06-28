package com.example.bcedu.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bcedu.Model.CapDoTuVung;
import com.example.bcedu.Model.KetQuaOnTap;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CapDoTuVungAdapter extends BaseAdapter {
    List<CapDoTuVung> array;
    Context context;

    ApiHocTap apiHocTap;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    public CapDoTuVungAdapter(List<CapDoTuVung> array, Context context) {
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

    public class MyViewHolderCapDo{
        TextView tenCapDo, soLuongTuVung, diemCaoNhat;
        ImageView hinhAnh;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
        int macapdo = position + 1;

        MyViewHolderCapDo viewHolder;

        if (convertView == null) {
            viewHolder = new MyViewHolderCapDo();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_cap_do_tu_vung, parent, false);
            viewHolder.tenCapDo = convertView.findViewById(R.id.tencapdo);
            viewHolder.soLuongTuVung = convertView.findViewById(R.id.soluongtu);
            viewHolder.diemCaoNhat = convertView.findViewById(R.id.diemCuaToi);
            viewHolder.hinhAnh = convertView.findViewById(R.id.hinh_mota_capdotuvung);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyViewHolderCapDo) convertView.getTag();
            compositeDisposable.clear();
        }

        viewHolder.tenCapDo.setText(array.get(position).getTencapdo());
        viewHolder.soLuongTuVung.setText("Số lượng từ vựng: " + array.get(position).getSoluong());
        Glide.with(context).load(array.get(position).getHinhanhmota()).into(viewHolder.hinhAnh);

        // Thiết lập mặc định cho viewHolder.diemCaoNhat
        viewHolder.diemCaoNhat.setVisibility(View.VISIBLE);
        viewHolder.diemCaoNhat.setText("Kỷ lục của tôi: N/A");

        // Sử dụng RxJava để cập nhật điểm hiện tại không đồng bộ
        compositeDisposable.add(apiHocTap.getKetQuaOnTapAPI(macapdo, Utils.nguoiDungCurrent.getManguoidung())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        ketQuaOnTapModel -> {
                            if (ketQuaOnTapModel.isSuccess() && !ketQuaOnTapModel.getResult().isEmpty()) {
                                KetQuaOnTap ketQuaOnTap = ketQuaOnTapModel.getResult().get(0);
                                float diemHienTai = ketQuaOnTap.getDiemcaonhat();

                                if (diemHienTai > 0) {
                                    viewHolder.diemCaoNhat.setText("Kỷ lục của tôi: " + diemHienTai);
                                    viewHolder.diemCaoNhat.setVisibility(View.VISIBLE);
                                } else {
                                    viewHolder.diemCaoNhat.setVisibility(View.GONE);
                                }
                            } else {
                                Log.d("logCDTV", "Không kết nối được với server! Lỗi: " + ketQuaOnTapModel.getMessage());
                                viewHolder.diemCaoNhat.setVisibility(View.GONE);
                            }
                        },
                        throwable -> {
                            Log.e("logCDTV", "Không kết nối được với server! Lỗi: " + throwable.getMessage());
                            viewHolder.diemCaoNhat.setVisibility(View.GONE);
                        }
                ));

        return convertView;
    }

}
