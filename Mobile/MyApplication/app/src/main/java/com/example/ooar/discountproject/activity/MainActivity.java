package com.example.ooar.discountproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ooar.discountproject.util.RetrofitConfiguration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final RetrofitConfiguration retrofitConfiguration = new RetrofitConfiguration();

        String tokenKey = getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
        if (tokenKey.equals("")) {//oturum yoksa
            Intent intent = new Intent(this, RegisterActivity.class);
            this.startActivity(intent);
        } else { //oturum varsa
            Intent intent = new Intent(this, UserActivity.class);
            this.startActivity(intent);
        }
    }
}
