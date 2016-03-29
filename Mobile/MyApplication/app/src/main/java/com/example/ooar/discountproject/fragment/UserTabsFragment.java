package com.example.ooar.discountproject.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.example.ooar.discountproject.R;

/**
 * Created by Onur Kuru on 25.3.2016.
 */
public class UserTabsFragment extends Fragment {

    public static FragmentTabHost mTabHost = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mTabHost == null) {
            return inflater.inflate(R.layout.user_tabs_layout, container, false);
        } else return mTabHost;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        if (mTabHost == null) {
            mTabHost = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);
            mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

            mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Bildirimler", null), NotificationsFragment.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Tercihler", null), ChoisesFragment.class, null);
        }
    }
}
