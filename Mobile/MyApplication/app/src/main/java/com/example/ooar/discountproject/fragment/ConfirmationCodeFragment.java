package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
 * Created by Onur Kuru on 6.3.2016.
 */
public class ConfirmationCodeFragment extends Fragment {

    TextView textTelNo;
    EditText confirmationCodeInput;
    Button sendConfirmationCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.confirmation_code_layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        setOnclickButton(view);
    }

    public void setOnclickButton(final View view) {
        String phoneNumber = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("phoneNumber", "");

        if (!phoneNumber.equals("")) {
            textTelNo = (TextView) view.findViewById(R.id.textTelNo);
            sendConfirmationCode = (Button) view.findViewById(R.id.sendConfirmationCode);
            confirmationCodeInput = (EditText) view.findViewById(R.id.confirmationCodeInput);

            textTelNo.setText(phoneNumber);

            final Callback callback = new Callback() {
                @Override
                public void success(Object o, Response response) {
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(new CreateProfilFragment());
                }

                @Override
                public void failure(RetrofitError error) {

                }
            };

            sendConfirmationCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Map<String, Object> validationMap = new Hashtable<String, Object>();
                    validationMap.put("confirmationCode", String.valueOf(confirmationCodeInput.getText()));

                    if (Util.checkValidation(validationMap)) {
                        RetrofitConfiguration.getRetrofitService().checkConfirmationCode(String.valueOf(confirmationCodeInput.getText()), callback);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Hatalı Giriş", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
