package com.example.ooar.discountproject.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.UserActivity;

/**
 * Created by Onur Kuru on 25.3.2016.
 */
public class UserTabsFragment extends Fragment {

    public static FragmentTabHost mTabHost = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mTabHost == null) {//tabhost yoksa içerik basılıyor varsa tabhost basılıyor
            return inflater.inflate(R.layout.user_tabs_layout, container, false);
        } else return mTabHost;

    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        if (mTabHost == null) {
            mTabHost = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);//tabhost oluşturuluyor
            mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

            mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Bildirimler", null), NotificationsFragment.class, null);//bildirimler ve tercihler fragmenti taba gömülüyor
            mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Tercihler", null), ChoisesFragment.class, null);
            UserTabsFragment.mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                public void onTabChanged(String tabId) {
                    switch (UserTabsFragment.mTabHost.getCurrentTab()) {//tab değişim eventları
                        case 0:
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Bildirimler");
                            UserActivity.mTitle = "Bildirimler";
                            break;
                        case 1:
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Tercihler");
                            UserActivity.mTitle = "Tercihler";
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }
}
