package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.RegisterActivity;
import com.example.ooar.discountproject.util.CookieHandler;
import com.example.ooar.discountproject.util.ErrorHandler;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import java.io.Serializable;
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
    private RetrofitConfiguration retrofitConfiguration = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String cookieKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("cookieKey", "");
        String cookieValue = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("cookieValue", "");

        if (!cookieKey.equals("") && !cookieValue.equals("")) {
            retrofitConfiguration = new RetrofitConfiguration(cookieKey, cookieValue);
        }

        return inflater.inflate(R.layout.phone_number, container, false);//content basılıyor
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        editText = (EditText) view.findViewById(R.id.telNo);
        editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        setOnClickButton();
        setChangeText();
    }

    //postConfirmationCode butonuna tıklandığında server a istek yapan event
    public void setOnClickButton() {
        final String[] phoneNumber = new String[1];
        final Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                Util.stopProgressDialog();
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).edit();
                editor.putString("phoneNumber", phoneNumber[0]).commit();

                Toast.makeText(getActivity(), o.toString(), Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();

                bundle.putSerializable("map", (Serializable) CookieHandler.readCookies(response));
                Fragment fragment = new ConfirmationCodeFragment();
                fragment.setArguments(bundle);
                FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                fc.replaceFragment(fragment, null);
            }

            @Override
            public void failure(RetrofitError error) {
                ErrorHandler.handleError(PhoneNumberFragment.this.getActivity(), error);
            }
        };

        button = (Button) getView().findViewById(R.id.postConfirmationCode);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> validationMapper = new Hashtable<String, Object>();
                phoneNumber[0] = editText.getText().toString().replace("(", "").replace(")", "").replace("-", "").replace(" ", "");
                validationMapper.put("phoneNumber", phoneNumber[0]);
                if (Util.checkValidation(validationMapper)) {
                    Util.startProgressDialog();
                    if (retrofitConfiguration != null) {
                        retrofitConfiguration.getRetrofitService(true).getConfirmationCode(String.valueOf(phoneNumber[0]), callback);
                    } else {
                        RetrofitConfiguration.getRetrofitService(true).getConfirmationCode(String.valueOf(phoneNumber[0]), callback);
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Hatalı Giriş", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //text uzunluğunu kontrol eden kısım
    public void setChangeText() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tempText = s.toString();
                if (tempText.length() > 14) {
                    tempText = tempText.substring(0, 14);
                    editText.setText(tempText);
                    editText.setSelection(start);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
