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
        if (regId.equals("")) {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            editor.putString("regId", refreshedToken).commit();
        }
        successRegistration();
    }

    public void successRegistration() {

        String phoneNumber = getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("phoneNumber", "");
        String tokenKey = getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
        if (phoneNumber.equals("") || tokenKey.equals("") || tokenKey.equals("err")) {//telefonda tokenkey ve telefon numarası yoksa register işlemleri başlatılıyor
            Intent intent = new Intent(this, RegisterActivity.class);
            this.startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            if (notificationId != 0) {
                intent.putExtra("notificationId", notificationId);
            }
            MainActivity.this.startActivity(intent);
        }

    }
}
