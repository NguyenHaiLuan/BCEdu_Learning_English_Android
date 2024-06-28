package com.example.bcedu.Retrofit;

import com.example.bcedu.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static Retrofit instance;

    public static Retrofit getInstance(String baseUrl){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }
        return instance;
    }
}
