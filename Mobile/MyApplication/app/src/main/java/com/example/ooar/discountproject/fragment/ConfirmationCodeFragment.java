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
    public void onStart() {
        super.onStart();
        String phoneNumber = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("phoneNumber", "");

        if (!phoneNumber.equals("")) {
            textTelNo = (TextView) getActivity().findViewById(R.id.textTelNo);
            sendConfirmationCode = (Button) getActivity().findViewById(R.id.sendConfirmationCode);
            confirmationCodeInput = (EditText) getActivity().findViewById(R.id.confirmationCodeInput);

            textTelNo.setText(phoneNumber);

            sendConfirmationCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity().getApplicationContext(), "Başarılı", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}
