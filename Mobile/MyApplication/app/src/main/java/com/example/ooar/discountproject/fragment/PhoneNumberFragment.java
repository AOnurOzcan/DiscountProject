package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import java.util.Hashtable;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Onur Kuru on 5.3.2016.
 */
public class PhoneNumberFragment extends Fragment {

    Button button;
    EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.phone_number, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        setOnClickButton(view);
    }

    public void setOnClickButton(final View view) {
        final Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                SharedPreferences settings = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("phoneNumber", String.valueOf(editText.getText())).commit();

                FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                fc.replaceFragment(new ConfirmationCodeFragment());
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Bir Hata Oluştu!", Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        button = (Button) getView().findViewById(R.id.postConfirmationCode);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText = (EditText) view.findViewById(R.id.telNo);

                Map<String, Object> validationMapper = new Hashtable<String, Object>();
                validationMapper.put("phoneNumber", String.valueOf(editText.getText()));
                if (Util.checkValidation(validationMapper)) {
                    RetrofitConfiguration.getRetrofitService().getConfirmationCode(String.valueOf(editText.getText()), callback);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Hatalı Giriş", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}
