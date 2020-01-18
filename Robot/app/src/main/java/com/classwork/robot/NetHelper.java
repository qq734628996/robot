package com.classwork.robot;


import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetHelper {

    private static NetHelper instance;

    public static NetHelper getInstance() {
        if (instance == null) {
            instance = new NetHelper();
        }
        return instance;
    }

    private Retrofit retrofit;

    private NetHelper() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.addConverterFactory(GsonConverterFactory.create(new Gson()));
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        builder.baseUrl("http://openapi.tuling123.com/");
        retrofit = builder.build();
    }

    private final static String TU_LING_URL = "http://openapi.tuling123.com/openapi/api/v2";

    Observable<TulingResultBody> sendMessage(TulingRequstBody body) {
        return retrofit.create(TulingApi.class).sendMessage(TU_LING_URL, body)
                .subscribeOn(Schedulers.io());
    }
}
