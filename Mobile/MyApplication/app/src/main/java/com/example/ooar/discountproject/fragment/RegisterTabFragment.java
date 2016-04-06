package com.example.ooar.discountproject.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.model.ConfirmationCode;

/**
 * Created by Onur Kuru on 6.4.2016.
 */
public class RegisterTabFragment extends Fragment {

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
            mTabHost.setup(getActivity(), getActivity().getSupportFragmentManager(), android.R.id.tabcontent);

            mTabHost.addTab(mTabHost.newTabSpec("home").setIndicator("Giriş", null), HomeFragment.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("phoneNumber").setIndicator("", null), PhoneNumberFragment.class, null);
        }
    }
}
