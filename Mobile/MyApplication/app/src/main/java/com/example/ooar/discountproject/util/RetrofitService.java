package com.example.ooar.discountproject.util;

import android.text.Editable;

import com.example.ooar.discountproject.model.Category;
import com.example.ooar.discountproject.model.City;
import com.example.ooar.discountproject.model.Company;
import com.example.ooar.discountproject.model.CompanyCategory;
import com.example.ooar.discountproject.model.ConfirmationCode;
import com.example.ooar.discountproject.model.User;
import com.squareup.okhttp.Call;

import java.util.List;
import java.util.Map;

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

    @POST("/checkconfirmationcode")
    void checkConfirmationCode(@Body ConfirmationCode confirmationCode, Callback<Map<String, String>> callback);

    @GET("/sendconfirmationcode/{phoneNumber}")
    void getConfirmationCode(@Path("phoneNumber") String phoneNumber, Callback<Object> callback);

    @GET("/city/all")
    void getAllCity(Callback<List<City>> callback);

    @POST("/user/createprofil")
    void createUser(@Body User user, Callback<Object> callback);

    @GET("/getAllCategories")
    void getAllCategories(Callback<List<Category>> callback);

    @GET("/company/withcategory")
    void getAllCompanyWithCategory(Callback<List<CompanyCategory>> callback);

    @POST("/user/createpreferences")
    void createUserPreferences(@Query("tokenKey") String tokenKey, @Body List<CompanyCategory> selectedCompanies, Callback<Object> callback);
}
