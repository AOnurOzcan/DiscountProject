package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.UserActivity;
import com.example.ooar.discountproject.model.ConfirmationCode;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

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
        final String phoneNumber = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("phoneNumber", "");

        if (!phoneNumber.equals("")) {
            textTelNo = (TextView) view.findViewById(R.id.textTelNo);
            sendConfirmationCode = (Button) view.findViewById(R.id.sendConfirmationCode);
            confirmationCodeInput = (EditText) view.findViewById(R.id.confirmationCodeInput);

            textTelNo.setText(phoneNumber);

            final Callback callback = new Callback() {
                @Override
                public void success(Object o, Response response) {

                    String confirmationCodeResponse = null, tokenKeyResponse = null;
                    Map<String, String> serverResponse = (Map<String, String>) o;
                    for (Map.Entry<String, String> entry : serverResponse.entrySet()) {
                        if (entry.getKey().equals("confirmationCode")) {
                            confirmationCodeResponse = entry.getValue();
                        } else if (entry.getKey().equals("tokenKey")) {
                            tokenKeyResponse = entry.getValue();
                        }
                    }
                    if (confirmationCodeResponse != null && tokenKeyResponse != null) {
                        if (confirmationCodeResponse.equals("err") && tokenKeyResponse.equals("err")) {//onay kodu yanlış
                            Toast.makeText(getActivity(), "Onay kodu yanlış tekrar deneyiniz", Toast.LENGTH_LONG).show();
                        } else if (confirmationCodeResponse.equals("true") && tokenKeyResponse.equals("err")) {//onay kodu doğru kullanıcı ilk kez giriş yapıyor
                            FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                            fc.replaceFragment(new CreateProfilFragment());
                        } else if (confirmationCodeResponse.equals("true")) {//onay kodu doğru kullanıcı için oturum oluşturuldu
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).edit();
                            editor.putString("tokenKey", tokenKeyResponse).commit();

                            Intent intent = new Intent(getActivity(), UserActivity.class);
                            getActivity().startActivity(intent);
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                }
            };

            sendConfirmationCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Map<String, Object> validationMap = new Hashtable<String, Object>();
                    validationMap.put("confirmationCode", String.valueOf(confirmationCodeInput.getText()));

                    if (Util.checkValidation(validationMap)) {
                        ConfirmationCode confirmationCode = new ConfirmationCode();
                        confirmationCode.setConfirmationCode(Integer.valueOf(String.valueOf(confirmationCodeInput.getText())));
                        confirmationCode.setPhoneNumber(phoneNumber);

                        RetrofitConfiguration.getRetrofitService().checkConfirmationCode(confirmationCode, callback);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Hatalı Giriş", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
