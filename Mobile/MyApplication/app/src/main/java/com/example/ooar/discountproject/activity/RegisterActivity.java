package com.example.ooar.discountproject.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Button;
import android.widget.EditText;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.fragment.PhoneNumberFragment;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.Util;


/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class RegisterActivity extends FragmentActivity implements FragmentChangeListener {

    Button button;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        Util.setProgressDialog(this);
        replaceFragment(new PhoneNumberFragment());
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.changeRegisterFragment, fragment);
        fragmentTransaction.commit();
    }
}
