package com.example.ooar.discountproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import java.util.Hashtable;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RetrofitConfiguration retrofitConfiguration = new RetrofitConfiguration();
        Util.setProgressDialog(this);
        setEvents();
    }

    public void setEvents() {
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
    protected void onRestart() {//Düzenlenecek
        super.onRestart();
        finish();
    }
}
