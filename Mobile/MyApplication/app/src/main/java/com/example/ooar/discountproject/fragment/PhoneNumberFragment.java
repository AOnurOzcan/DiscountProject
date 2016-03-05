package com.example.ooar.discountproject.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.RetrofitConfiguration;

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
    public void onStart() {
        super.onStart();
        final Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                Toast.makeText(getActivity().getApplicationContext(), "Başaralı", Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity().getApplicationContext(), o.toString(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity().getApplicationContext(), "Tıklandı", Toast.LENGTH_LONG).show();
                editText = (EditText) getView().findViewById(R.id.telNo);
                RetrofitConfiguration.getRetrofitService().getConfirmationCode(String.valueOf(editText.getText()), callback);
            }
        });
    }

}
