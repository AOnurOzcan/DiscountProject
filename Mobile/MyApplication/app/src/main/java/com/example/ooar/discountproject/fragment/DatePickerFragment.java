package com.example.ooar.discountproject.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.ooar.discountproject.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ramos on 08.03.2016.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    Spinner editText;

    public DatePickerFragment() {
        this.setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year, month, day;
        editText = (Spinner) getActivity().findViewById(R.id.birthDayEditText);

        if (editText.getSelectedItem().toString().equals("")) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else {
            String[] dateText = editText.getSelectedItem().toString().split("/");
            day = Integer.valueOf(dateText[0]);
            month = Integer.valueOf(dateText[1])-1;
            year = Integer.valueOf(dateText[2]);
        }

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        List<String> adapterString = new ArrayList<>();
        month=month+1;
        adapterString.add(day + "/" + month + "/" + year);

        ArrayAdapter adapter = new ArrayAdapter(getActivity().getApplicationContext(), R.layout.spinner_item, adapterString);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        editText = (Spinner) getActivity().findViewById(R.id.birthDayEditText);
        editText.setAdapter(adapter);
        editText.setSelection(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CreateProfilFragment.datePickerIsShow = false;
        ProfileFragment.datePickerIsShow=false;
    }
}