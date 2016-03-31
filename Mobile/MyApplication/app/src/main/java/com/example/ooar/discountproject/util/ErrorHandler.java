package com.example.ooar.discountproject.util;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.ooar.discountproject.activity.MainActivity;
import com.example.ooar.discountproject.activity.UserActivity;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Onur Kuru on 31.3.2016.
 */
public class ErrorHandler {
    public static void handleError(Activity activity, RetrofitError retrofitError) {

        Util.stopProgressDialog();
        Response response = retrofitError.getResponse();

        if (response != null) {
            switch (response.getStatus()) {
                case 401://unauthorized
                    Toast.makeText(activity.getApplicationContext(), "Yetkisiz Erişim Algılandı! Oturumunuz Kapatılacak! Oturum açıp yeniden deneyin", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = activity.getSharedPreferences("Session", Activity.MODE_PRIVATE).edit();
                    editor.remove("phoneNumber");
                    editor.remove("tokenKey");
                    editor.commit();
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                    activity.finish();

                case 400://badRequest
                case 500://server error
            }
        } else if (retrofitError.isNetworkError()) {
            if (retrofitError.getCause() instanceof SocketTimeoutException) {//time out

            } else {
                //No Connection
            }
        }
    }
}
