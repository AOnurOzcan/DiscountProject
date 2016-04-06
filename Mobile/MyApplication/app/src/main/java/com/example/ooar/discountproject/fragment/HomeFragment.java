package com.example.ooar.discountproject.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.RegisterActivity;
import com.example.ooar.discountproject.activity.UserActivity;
import com.example.ooar.discountproject.util.FragmentChangeListener;

/**
 * Created by Onur Kuru on 6.4.2016.
 */
public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        Button startRegisterButton = (Button) getActivity().findViewById(R.id.startRegister);
        startRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.registerTabFragment.mTabHost.setCurrentTab(1);
            }
        });
    }
}
