package com.example.ooar.discountproject.util;

import android.text.Editable;

import com.example.ooar.discountproject.model.Category;
import com.example.ooar.discountproject.model.City;
import com.example.ooar.discountproject.model.Company;
import com.example.ooar.discountproject.model.CompanyCategory;
import com.example.ooar.discountproject.model.ConfirmationCode;
import com.example.ooar.discountproject.model.Notification;
import com.example.ooar.discountproject.model.Preference;
import com.example.ooar.discountproject.model.User;
import com.example.ooar.discountproject.model.UserNotification;
import com.example.ooar.discountproject.model.UserProduct;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Onur Kuru on 5.3.2016.
 */


public interface RetrofitService {

    @POST("/confirmationcode/check")
    void checkConfirmationCode(@Body ConfirmationCode confirmationCode, Callback<Map<String, String>> callback);

    @GET("/confirmationcode/send/{phoneNumber}")
    void getConfirmationCode(@Path("phoneNumber") String phoneNumber, Callback<Object> callback);

    @GET("/city/all")
    void getAllCity(Callback<List<City>> callback);

    @POST("/user/profile/create")
    void createUser(@Body User user, Callback<Object> callback);

    @GET("/user/profile/get")
    void getUser(@Query("tokenKey") String tokenKey, Callback<User> callback);

    @PUT("/user/profile/edit")
    void editUser(@Query("tokenKey") String tokenKey, @Body User user, Callback<User> callback);

    @GET("/mobile/mainCategoryWithSubs")
    void getAllCategories(Callback<List<Category>> callback);

    @GET("/companies/withcategory")
    void getAllCompanyWithCategory(Callback<List<CompanyCategory>> callback);

    @POST("/user/preference/create")
    void createUserPreferences(@Query("tokenKey") String tokenKey, @Body List<CompanyCategory> selectedCompanies, Callback<Object> callback);

    @POST("/user/preference/delete")
    void deleteUserPreferences(@Query("tokenKey") String tokenKey, @Body List<Preference> selectedCompanies, Callback<Object> callback);

    @GET("/user/preference/all")
    void getUserPreferences(@Query("tokenKey") String tokenKey, Callback<List<Preference>> callback);

    @POST("/session/create")
    void createSession(@Body Map<String, String> phoneNumber, Callback<Map<String, String>> callback);

    @GET("/user/notification/{id}")
    void getNotificationById(@Query("tokenKey") String tokenKey, @Path("id") Integer notificationId, Callback<Notification> callback);

    @GET("/notification")
    void getAllNotification(@Query("tokenKey") String tokenKey, Callback<List<UserNotification>> callback);

    @GET("/session/delete")
    void deleteSession(@Query("tokenKey") String tokenKey, Callback<Object> callback);

    @POST("/user/product/create")
    void createUserProduct(@Query("tokenKey") String tokenKey, @Body UserProduct userProduct, Callback<Object> callback);

    @GET("/user/products/all")
    void getAllUserProducts(@Query("tokenKey") String tokenKey, Callback<List<UserProduct>> callback);

    @DELETE("/user/product/delete/{id}")
    void deleteUserProduct(@Query("tokenKey") String tokenKey, @Path("id") int id, Callback<Object> callback);

}
