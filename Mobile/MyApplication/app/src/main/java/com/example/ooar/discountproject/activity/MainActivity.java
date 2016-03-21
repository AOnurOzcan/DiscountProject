package com.example.ooar.discountproject.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "279341591262";

    GoogleCloudMessaging gcm;
    Context context;
    String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RetrofitConfiguration retrofitConfiguration = new RetrofitConfiguration();
        Util.setProgressDialog(this);
        context = getApplicationContext();
        setEvents();
    }

    public void setEvents() {

        String regId = getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("regId", "");
        if (checkPlayServices()) {
            if (regId.equals("")) {
                new Register().execute();
            } else {
                successRegistration();
            }
        }
    }

    public void successRegistration() {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                Util.stopProgressDialog();
                Map<String, String> mapper = (Map<String, String>) o;
                String tokenKey = mapper.get("tokenKey");
                if (tokenKey.equals("") || tokenKey.equals("err")) {//Gönderilen numaraya ait kullanıcı yok
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    MainActivity.this.startActivity(intent);
                } else {//Oturum var token güncellendi
                    SharedPreferences.Editor editor = getSharedPreferences("Session", Activity.MODE_PRIVATE).edit();
                    editor.putString("tokenKey", tokenKey).commit();
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    MainActivity.this.startActivity(intent);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Util.stopProgressDialog();
                Toast.makeText(MainActivity.this, "Sunucudan Yanıt Alınamadı", Toast.LENGTH_SHORT).show();
            }
        };

        String phoneNumber = getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("phoneNumber", "");
        if (phoneNumber.equals("")) {
            Intent intent = new Intent(this, RegisterActivity.class);
            this.startActivity(intent);
        }
        Map<String, String> mapper = new Hashtable<>();
        mapper.put("phoneNumber", phoneNumber);
        RetrofitConfiguration.getRetrofitService(true).createSession(mapper, callback);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    public class Register extends AsyncTask {
        boolean success = true;

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regId = gcm.register(SENDER_ID);
                SharedPreferences.Editor editor = getSharedPreferences("Session", Activity.MODE_PRIVATE).edit();
                editor.putString("regId", regId).commit();
            } catch (IOException ex) {
                this.success = false;
            }
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
