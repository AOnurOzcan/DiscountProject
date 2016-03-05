package com.example.ooar.discountproject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class RetrofitConfiguration {

    private static final String API_URL = "http://10.0.2.2:3032";
    private static RetrofitService retrofitService;

    public RetrofitConfiguration(){
        Gson gson = new GsonBuilder().create();
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(API_URL).setConverter(new GsonConverter(gson)).build();
        retrofitService = restAdapter.create(RetrofitService.class);
    }

    public static RetrofitService getRetrofitService() {
        return retrofitService;
    }

}
