package com.example.ooar.discountproject.util;

import com.example.ooar.discountproject.model.Company;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Onur Kuru on 5.3.2016.
 */


public interface RetrofitService {

    @GET("/test")
    void deneme(Callback<List<Company>> callback);

    @GET("/sendconfirmationcode/{phoneNumber}")
    void getConfirmationCode(@Path("phoneNumber")String phoneNumber,Callback<Object> callback);
}
