package com.example.bcedu.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.bcedu.Adapter.AdapterAllNguoiDung;
import com.example.bcedu.Adapter.BangXHAdapter;
import com.example.bcedu.Adapter.KhoaHocTheoGiangVienAdapter;
import com.example.bcedu.Adapter.NguoiDungAdapter;
import com.example.bcedu.Model.NguoiDung;
import com.example.bcedu.R;
import com.example.bcedu.Retrofit.ApiHocTap;
import com.example.bcedu.Retrofit.RetrofitClient;
import com.example.bcedu.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChallengerFragment extends Fragment {
    private RecyclerView recyclerViewUsers;
    private BangXHAdapter adapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiHocTap apiHocTap;
    private List<NguoiDung> mangNguoiDung = new ArrayList<>();

    public ChallengerFragment() {
        // Required empty public constructor
    }

    public static ChallengerFragment newInstance(String param1, String param2) {
        ChallengerFragment fragment = new ChallengerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiHocTap = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiHocTap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenger, container, false);
        recyclerViewUsers = view.findViewById(R.id.recyclerView_thachdau);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        loadUsers();
        return view;
    }

    private void loadUsers() {
        compositeDisposable.add(apiHocTap.getNguoiDungTheoMaXepHang(10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loadModel -> {
                            if (loadModel.isSuccess()) {
                                mangNguoiDung = loadModel.getResult();
                                handleResponse();
                            } else {
                                StyleableToast.makeText(getContext(), loadModel.getMessage(), R.style.warning).show();
                            }
                        }, throwable -> {
                            StyleableToast.makeText(getContext(), "Lỗi: " + throwable.getMessage(), R.style.error_toast).show();
                            Log.e("Loi", "DangKi: " + throwable.getMessage());
                        }
                ));
    }

    private void handleResponse() {
        adapter = new BangXHAdapter(mangNguoiDung, getContext());
        recyclerViewUsers.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
