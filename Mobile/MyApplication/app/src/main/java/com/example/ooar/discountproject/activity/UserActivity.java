package com.example.ooar.discountproject.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.support.v7.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.adapter.UserPagerAdapter;
import com.example.ooar.discountproject.fragment.ProfileFragment;

import java.util.Calendar;

/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class UserActivity extends AppCompatActivity  implements ActionBar.TabListener {

    private UserPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_layout);

        final String[] tabTitles =getResources().getStringArray(R.array.tab_title_array);
        pagerAdapter=new UserPagerAdapter(getSupportFragmentManager());
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewPager=(ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                actionBar.setTitle(tabTitles[position]);
            }
        });
        actionBar.addTab(actionBar.newTab().setTabListener(this).setIcon(R.drawable.notification));
        actionBar.addTab(actionBar.newTab().setTabListener(this).setIcon(R.drawable.choises));
        actionBar.addTab(actionBar.newTab().setTabListener(this).setIcon(R.drawable.profile));
    }
    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }
}
