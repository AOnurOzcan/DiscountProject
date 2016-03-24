package com.example.ooar.discountproject.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.model.Branch;

/**
 * Created by Onur Kuru on 24.3.2016.
 */
public class BranchDetailFragment extends Fragment {

    public static Branch branch;
    TextView branchName;
    TextView branchPhone;
    TextView branchAdress;
    TextView branchWorkHour;
    Button branchMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.branch_detail, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        branchName = (TextView) view.findViewById(R.id.branchName);
        branchPhone = (TextView) view.findViewById(R.id.branchPhone);
        branchAdress = (TextView) view.findViewById(R.id.branchAdress);
        branchWorkHour = (TextView) view.findViewById(R.id.branchWorkHour);
        branchMap = (Button) view.findViewById(R.id.branchMap);

        branchName.setText(branch.getName());
        branchPhone.setText(branch.getPhone());
        branchAdress.setText(branch.getAddress());
        branchWorkHour.setText(branch.getWorkingHours());
        branchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(branch.getLocationURL()));
                startActivity(intent);
            }
        });
    }
}
