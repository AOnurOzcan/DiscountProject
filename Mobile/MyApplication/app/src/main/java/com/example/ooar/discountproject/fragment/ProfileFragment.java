package com.example.ooar.discountproject.fragment;

        import android.app.DialogFragment;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.ooar.discountproject.R;
        import com.example.ooar.discountproject.util.RetrofitConfiguration;

        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.List;


/**
 * Created by Ramos on 06.03.2016.
 */
public class ProfileFragment  extends Fragment {
    EditText editText;
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.profile,container,false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        editText = (EditText) getView().findViewById(R.id.birthDayEditText);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
    }
    public void showDatePickerDialog(View v ) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getFragmentManager(), "datePicker");
    }


}
