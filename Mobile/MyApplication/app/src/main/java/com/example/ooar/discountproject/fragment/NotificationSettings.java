package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.model.User;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Onur Kuru on 29.3.2016.
 */
public class NotificationSettings extends Fragment {

    public User tempUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notification_settings, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        if (ProfileFragment.thisUser == null) {
            getUser();
        } else {
            renderPage();
        }
    }

    public void getUser() {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                ProfileFragment.thisUser = (User) o;
                ProfileFragment.thisUser.setBirthday(Util.parseDate(ProfileFragment.thisUser.getBirthday()));
                Util.stopProgressDialog();
                renderPage();
            }

            @Override
            public void failure(RetrofitError error) {
                Util.stopProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), "Bir Hata Oluştu!", Toast.LENGTH_LONG).show();
            }
        };

        String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
        RetrofitConfiguration.getRetrofitService(true).getUser(tokenKey, callback);
    }

    public void updateUser(User user) {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                Util.stopProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), "Başarıyla Kaydedildi", Toast.LENGTH_LONG).show();
                ProfileFragment.thisUser.setNotificationOpen(tempUser.isNotificationOpen());
            }

            @Override
            public void failure(RetrofitError error) {
                Util.stopProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), "Bir Hata Oluştu!", Toast.LENGTH_LONG).show();
                tempUser.setNotificationOpen(ProfileFragment.thisUser.isNotificationOpen());
            }
        };
        String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
        RetrofitConfiguration.getRetrofitService(true).editUser(tokenKey, user, callback);
    }

    public void renderPage() {
        tempUser = ProfileFragment.thisUser;
        Switch notificationSwitch = (Switch) getActivity().findViewById(R.id.companyNotifications);
        boolean notificationIsOpen = tempUser.isNotificationOpen();
        notificationSwitch.setChecked(notificationIsOpen);

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tempUser.setNotificationOpen(isChecked);
                updateUser(tempUser);
            }
        });
    }
}
