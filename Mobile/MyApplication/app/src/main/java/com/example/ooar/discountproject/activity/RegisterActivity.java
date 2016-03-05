package com.example.ooar.discountproject.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.EditText;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.fragment.PhoneNumberFragment;


/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class RegisterActivity extends FragmentActivity {

    Button button;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        Fragment fragment = new PhoneNumberFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.phoneNumberFragment, fragment);
        fragmentTransaction.commit();
    }

}
