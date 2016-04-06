package com.example.ooar.discountproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;
import android.widget.EditText;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.fragment.HomeFragment;
import com.example.ooar.discountproject.fragment.PhoneNumberFragment;
import com.example.ooar.discountproject.fragment.RegisterTabFragment;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.Util;


/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class RegisterActivity extends FragmentActivity implements FragmentChangeListener {

    public static RegisterTabFragment registerTabFragment = new RegisterTabFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);//content basılıp phononumberfragment çağrılıyor
        Util.setProgressDialog(this);
        replaceFragment(registerTabFragment, null);
    }

    @Override
    public void replaceFragment(Fragment fragment, String tagName) {//fragment değiştirme işlemlerini yapan fonksiyon
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (tagName != null) {
            fragmentTransaction.replace(R.id.changeRegisterFragment, fragment, tagName);
        } else {
            fragmentTransaction.replace(R.id.changeRegisterFragment, fragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);
    }
}
