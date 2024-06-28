package com.example.bcedu.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.example.bcedu.Model.NguoiDung;
import com.example.bcedu.Model.XepHang;

public class Utils {

    public static final String BASE_URL = "http://bcedu.id.vn/bcedu/";
    public static NguoiDung nguoiDungCurrent = new NguoiDung();
    public static NguoiDung nguoiDungDK = new NguoiDung();
    public static XepHang xepHangCuaToi = new XepHang();
    public static XepHang xepHangTiepTheo = new XepHang();

    private Utils() {
        // Private constructor để ngăn không cho khởi tạo đối tượng
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                if (capabilities != null) {
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                }
            }
        }
        return false;
    }

}
