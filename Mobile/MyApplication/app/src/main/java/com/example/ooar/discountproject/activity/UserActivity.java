package com.example.ooar.discountproject.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.fragment.UserTabsFragment;
import com.example.ooar.discountproject.util.FragmentChangeListener;

/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class UserActivity extends FragmentActivity implements FragmentChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);
        replaceFragment(new UserTabsFragment());
    }

    public void replaceFragment(Fragment fragment) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.userFragments, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }
}
