package com.example.ooar.discountproject.util;

import android.text.Editable;

import com.example.ooar.discountproject.model.City;
import com.example.ooar.discountproject.model.Company;
import com.example.ooar.discountproject.model.User;
import com.squareup.okhttp.Call;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Onur Kuru on 5.3.2016.
 */


public interface RetrofitService {

    @GET("/checkconfirmationcode/{confirmationCode}")
    void checkConfirmationCode(@Path("confirmationCode")String phoneNumber,Callback<Object> callback);

    @GET("/sendconfirmationcode/{phoneNumber}")
    void getConfirmationCode(@Path("phoneNumber")String phoneNumber,Callback<Object> callback);

    @GET("/city/all")
    void getAllCity(Callback<List<City>> callback);

    @POST("/user/createprofil")
    void createUser(@Body User user,Callback<Object> callback);
}
