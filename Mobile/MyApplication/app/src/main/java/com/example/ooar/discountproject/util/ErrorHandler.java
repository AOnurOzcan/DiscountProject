package com.example.ooar.discountproject.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.WindowManager;

import com.example.ooar.discountproject.activity.MainActivity;
import com.example.ooar.discountproject.activity.UserActivity;

import java.net.SocketTimeoutException;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Onur Kuru on 31.3.2016.
 */
//Retrofit error larını handle eden fonksiyon
public class ErrorHandler {
    public static void handleError(final Activity activity, RetrofitError retrofitError) {

        Util.stopProgressDialog();
        Response response = retrofitError.getResponse();//retofit cevabı alınıyor

        if (!isNetworkAvailable(activity)) {//hata ilk olarak kullanının internetinin olup olmadığına bakılarak karar veriliyor
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);//kullanıcının interneti yoksa ilgili alert dialog basılıyor
            builder.setCancelable(false);
            builder.setMessage("İnternet Bağlantınızı Kontrol Edip Tekrar Deneyiniz.");
            builder.create().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            builder.setPositiveButton("Tekrar Dene!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = null;
                    if (activity.getLocalClassName().equals("activity.UserActivity")) {//error un geldiği sınıf UserActivity ise UserActivity yenileniyor
                        UserActivity.removeAllCache();
                        UserActivity.reload = true;
                        intent = new Intent(activity, UserActivity.class);
                    } else if (activity.getLocalClassName().equals("activity.MainActivity")) {//error un geldiği sınıf MainActivity ise MainActivity yenileniyor
                        intent = new Intent(activity, MainActivity.class);
                    } else if (activity.getLocalClassName().equals("activity.RegisterActivity")) {//error un geldiği sınıf RegisterActivity ise MainActivity yenileniyor
                        intent = new Intent(activity, MainActivity.class);
                    }
                    activity.finish();
                    activity.startActivity(intent);
                }
            });
            builder.show();
        } else if (response != null) {//hata server kaynaklı ise buraya düşecek
            switch (response.getStatus()) {//hata kodu alınıyor
                case 401://unauthorized
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);//tokenkey ve phone değerleri silinip oturum açma sayfasına gönderiliyor
                    builder.setCancelable(false);
                    builder.setMessage("Yetkisiz Erişim Algılandı! Oturumunuz Kapatılacak! Oturum açıp yeniden deneyin.");
                    builder.create().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    builder.setPositiveButton("Oturumu Şimdi Kapat", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences.Editor editor = activity.getSharedPreferences("Session", Activity.MODE_PRIVATE).edit();
                            editor.remove("phoneNumber");
                            editor.remove("tokenKey");
                            editor.commit();
                            Intent intent = new Intent(activity, MainActivity.class);
                            dialog.dismiss();
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    });
                    builder.show();
                    break;
                case 400://badRequest
                case 500://server error
            }
        } else if (retrofitError.isNetworkError()) {//hata cevap gecilkmesi ise
            if (retrofitError.getCause() instanceof SocketTimeoutException) {//time out

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setCancelable(false);
                builder.setMessage("Sunucudan Yanıt Alınamadı.");
                builder.create().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                builder.setPositiveButton("Tekrar Dene!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = null;
                        if (activity.getLocalClassName().equals("activity.UserActivity")) {
                            UserActivity.removeAllCache();
                            UserActivity.reload = true;
                            intent = new Intent(activity, UserActivity.class);
                        } else if (activity.getLocalClassName().equals("activity.MainActivity")) {
                            intent = new Intent(activity, MainActivity.class);
                        }
                        activity.finish();
                        activity.startActivity(intent);
                    }
                });
                builder.show();
            } else {
                //No Connection
            }
        }
    }

    private static boolean isNetworkAvailable(Activity activity) {//kullanıcının interneti olup olmadığını kontrol eden fonksiyon
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
