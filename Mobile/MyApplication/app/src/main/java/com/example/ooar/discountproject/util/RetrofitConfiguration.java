package com.example.ooar.discountproject.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.ApacheClient;
import retrofit.converter.GsonConverter;

/**
 * Created by Onur Kuru on 5.3.2016.
 */
//Retrofit ayarları yapılıyor
public class RetrofitConfiguration {

    //        private static final String API_URL = "http://192.168.1.3";//local
    private static final String API_URL = "http://37.139.11.216";//remote

    private static RetrofitService retrofitService;

    public RetrofitConfiguration() {
        Gson gson = new GsonBuilder().create();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(API_URL).setConverter(new GsonConverter(gson)).build();
        retrofitService = restAdapter.create(RetrofitService.class);
    }

    public RetrofitConfiguration(final String cookieKey, final String cookieValue) {
        Gson gson = new GsonBuilder().create();
        RestAdapter restAdapter = new RestAdapter.Builder().setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Cookie", cookieKey + "=" + cookieValue);
            }
        }).setEndpoint(API_URL).setConverter(new GsonConverter(gson)).build();
        retrofitService = restAdapter.create(RetrofitService.class);
    }

    public static RetrofitService getRetrofitService() {
        return retrofitService;
    }

    public static RetrofitService getRetrofitService(boolean startProggress) {
        if (startProggress) {
            Util.startProgressDialog();
        }
        return retrofitService;
    }

}
