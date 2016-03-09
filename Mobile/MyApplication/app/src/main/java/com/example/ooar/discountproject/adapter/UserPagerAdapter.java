package com.example.ooar.discountproject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.ooar.discountproject.fragment.ChoisesFragment;
import com.example.ooar.discountproject.fragment.NotificationsFragment;
import com.example.ooar.discountproject.fragment.ProfileFragment;

/**
 * Created by Ramos on 06.03.2016.
 */
public class UserPagerAdapter extends FragmentStatePagerAdapter {
    public UserPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return new NotificationsFragment();
            case 1:
                return new ChoisesFragment();
            case 2:
                return new ProfileFragment();
        }
        throw new IllegalArgumentException("bilinmeyen fragment tabı ");
    }
    @Override
    public int getCount()
    {
        return 3;
    }
}
