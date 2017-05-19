package com.example.ooar.discountproject.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "279341591262";//gcm sender id

    //    GoogleCloudMessaging gcm;
    Context context;
    String regId;
    int notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RetrofitConfiguration retrofitConfiguration = new RetrofitConfiguration();
        Util.setProgressDialog(this);
        context = getApplicationContext();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            notificationId = extras.getInt("notificationId");
        }
        setEvents();

    }

    public void setEvents() {
        SharedPreferences.Editor editor = getSharedPreferences("Session", Activity.MODE_PRIVATE).edit();
        editor.putInt("NotificationCount", 0).commit();

        String regId = getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("regId", "");
//        if (checkPlayServices()) {//play servis kontrol ediliyor
        if (regId.equals("")) {
            new Register().execute();//telefonun register id si yoksa oluşturuluyor
        } else {
            successRegistration();
        }
//        }
    }

    public void successRegistration() {

        String phoneNumber = getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("phoneNumber", "");
        String tokenKey = getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
        if (phoneNumber.equals("") || tokenKey.equals("") || tokenKey.equals("err")) {//telefonda tokenkey ve telefon numarası yoksa register işlemleri başlatılıyor
            Intent intent = new Intent(this, RegisterActivity.class);
            this.startActivity(intent);
        } else {//Oturum varsa kullanıcı anasayfa başlatılıyor
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            if (notificationId != 0) {
                intent.putExtra("notificationId", notificationId);
            }
            MainActivity.this.startActivity(intent);
        }

    }

//    private boolean checkPlayServices() {//play service olup olmadığını kontrol eden fonksiyon
//        int resultCode = GoogleApiAvailability.makeGooglePlayServicesAvailable();
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }

    public class Register extends AsyncTask {//telefon için register id alan fonksiyon
        boolean success = true;

        @Override
        protected Object doInBackground(Object[] params) {

//                if (gcm == null) {
//                    gcm = GoogleCloudMessaging.getInstance(context);
//                }
//                regId = gcm.register(SENDER_ID);//register id alınıp sharedpreference ye yazılıyor

            String refreshedToken = FirebaseInstanceId.getInstance().getToken();

            SharedPreferences.Editor editor = getSharedPreferences("Session", Activity.MODE_PRIVATE).edit();
            editor.putString("regId", refreshedToken).commit();
            return this.success;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (this.success) {
                successRegistration();
            } else {
                Toast.makeText(getApplicationContext(), "Google Sunucuna Bağlanılamıyor", Toast.LENGTH_LONG).show();
            }
        }
    }
}
