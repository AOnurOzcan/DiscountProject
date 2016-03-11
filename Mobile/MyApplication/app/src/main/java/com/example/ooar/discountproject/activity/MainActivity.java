package com.example.ooar.discountproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ooar.discountproject.util.RetrofitConfiguration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        final RetrofitConfiguration retrofitConfiguration = new RetrofitConfiguration();

        boolean session = getSharedPreferences("Session", Activity.MODE_PRIVATE).getBoolean("session", false);

        if (session) {//oturum varsa
            Intent intent = new Intent(this, UserActivity.class);
            this.startActivity(intent);
        } else { //oturum yoksa
            Intent intent = new Intent(this, UserActivity.class);
            this.startActivity(intent);
        }
    }
}
