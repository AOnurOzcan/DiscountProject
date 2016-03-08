package com.example.ooar.discountproject.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.EditText;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.fragment.PhoneNumberFragment;
import com.example.ooar.discountproject.fragment.UserPreferencesFragment;
import com.example.ooar.discountproject.util.FragmentChangeListener;


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
        replaceFragment(new UserPreferencesFragment());
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.changeRegisterFragment, fragment);
        fragmentTransaction.commit();
    }
}
