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

//istek yapılacak url ler tanımlanıyor
public interface RetrofitService {

    @POST("/confirmationcode/check")
//onay kodunun gönderildiği servis
    void checkConfirmationCode(@Body ConfirmationCode confirmationCode, Callback<Map<String, String>> callback);

    @GET("/confirmationcode/send/{phoneNumber}")
//onay kodununalındığı servis
    void getConfirmationCode(@Path("phoneNumber") String phoneNumber, Callback<Object> callback);

    @GET("/city/all")
//tüm şehirleri getiren sernvis
    void getAllCity(Callback<List<City>> callback);

    @POST("/user/profile/create")
//kullanıcı için profil oluşturan servis
    void createUser(@Body User user, Callback<Object> callback);

    @GET("/user/profile/get")
//kullanıcının profilini getiren servis
    void getUser(@Query("tokenKey") String tokenKey, Callback<User> callback);

    @PUT("/user/profile/edit")
//kullanıcının profilini güncelleyen servis
    void editUser(@Query("tokenKey") String tokenKey, @Body User user, Callback<User> callback);

    @GET("/mobile/mainCategoryWithSubs")
//ana ve alt kategorilerigetiren servis
    void getAllCategories(Callback<List<Category>> callback);

    @GET("/companies/withcategory")
//firmaları kategorileri ile getiren servis
    void getAllCompanyWithCategory(Callback<List<CompanyCategory>> callback);

    @POST("/user/preference/create")
//kullanını tercihi kaydetme servisi
    void createUserPreferences(@Query("tokenKey") String tokenKey, @Body List<CompanyCategory> selectedCompanies, Callback<Object> callback);

    @POST("/user/preference/delete")
//kullanıcı stercihi silme servisi
    void deleteUserPreferences(@Query("tokenKey") String tokenKey, @Body List<Preference> selectedCompanies, Callback<Object> callback);

    @GET("/user/preference/all")
//kullanıcının tüm tercihlerini getiren servis
    void getUserPreferences(@Query("tokenKey") String tokenKey, Callback<List<Preference>> callback);

    @POST("/session/create")
//kullanıcnın oturumu oluşturuan servis
    void createSession(@Body Map<String, String> phoneNumber, Callback<Map<String, String>> callback);

    @GET("/user/notification/{id}")
//kullanıcı bildirim getiren servis
    void getNotificationById(@Query("tokenKey") String tokenKey, @Path("id") Integer notificationId, Callback<Notification> callback);

    @GET("/notification")
//kullanıcının tüm bildirimlerini getiren servis
    void getAllNotification(@Query("tokenKey") String tokenKey, Callback<List<UserNotification>> callback);

    @GET("/session/delete")
//oturum silen servis
    void deleteSession(@Query("tokenKey") String tokenKey, Callback<Object> callback);

    @POST("/user/product/create")
//alışveriş listesine ürün eklyen servis
    void createUserProduct(@Query("tokenKey") String tokenKey, @Body UserProduct userProduct, Callback<Object> callback);

    @GET("/user/products/all")
//alışveriş listesini getiren servis
    void getAllUserProducts(@Query("tokenKey") String tokenKey, Callback<List<UserProduct>> callback);

    @DELETE("/user/product/delete/{id}")
//alışveris listesinden ürün silen servis
    void deleteUserProduct(@Query("tokenKey") String tokenKey, @Path("id") int id, Callback<Object> callback);

}
