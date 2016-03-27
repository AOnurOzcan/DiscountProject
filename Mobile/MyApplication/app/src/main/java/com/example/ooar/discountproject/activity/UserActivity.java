package com.example.ooar.discountproject.activity;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.fragment.NotificationDetailFragment;
import com.example.ooar.discountproject.fragment.UserTabsFragment;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.Util;

/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class UserActivity extends FragmentActivity implements FragmentChangeListener {
    int notificationId;
    boolean isNotificationId = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);
        Bundle extras = getIntent().getExtras();
        Util.setProgressDialog(this);
        if(extras != null){
            notificationId = extras.getInt("notificationId");
        }
        if (notificationId > 0) {
            isNotificationId = true;
        }
        if (isNotificationId) {
            Bundle bundle = new Bundle();
            bundle.putInt("notificationId", notificationId);
            Fragment notificationDetailFragment = new NotificationDetailFragment();
            notificationDetailFragment.setArguments(bundle);

            replaceFragment(notificationDetailFragment);
        }
        else{
            replaceFragment(new UserTabsFragment());
        }

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.userFragments, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }
}
