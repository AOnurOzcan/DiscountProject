package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ooar.discountproject.R;

/**
 * Created by Ramos on 06.03.2016.
 */
public class ChoisesFragment  extends Fragment {
    @Override
      public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choises, container, false);
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Button btnProfile = (Button) view.findViewById(R.id.btnProfile);
        Button btnSettings = (Button) view.findViewById(R.id.btnSettings);
        Button btnPrivacy = (Button) view.findViewById(R.id.btnPrivacy);

        View panelProfile = view.findViewById(R.id.panelProfile);
        panelProfile.setVisibility(View.GONE);

        View panelSettings = view.findViewById(R.id.panelSettings);
        panelSettings.setVisibility(View.GONE);

        View panelPrivacy = view.findViewById(R.id.panelPrivacy);
        panelPrivacy.setVisibility(View.GONE);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelProfile = view.findViewById(R.id.panelProfile);
                panelProfile.setVisibility(View.VISIBLE);

                View panelSettings = view.findViewById(R.id.panelSettings);
                panelSettings.setVisibility(View.GONE);

                View panelPrivacy = view.findViewById(R.id.panelPrivacy);
                panelPrivacy.setVisibility(View.GONE);

            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelProfile = view.findViewById(R.id.panelProfile);
                panelProfile.setVisibility(View.GONE);

                View panelSettings = view.findViewById(R.id.panelSettings);
                panelSettings.setVisibility(View.VISIBLE);

                View panelPrivacy = view.findViewById(R.id.panelPrivacy);
                panelPrivacy.setVisibility(View.GONE);

            }
        });

        btnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                View panelProfile = view.findViewById(R.id.panelProfile);
                panelProfile.setVisibility(View.GONE);

                View panelSettings = view.findViewById(R.id.panelSettings);
                panelSettings.setVisibility(View.GONE);

                View panelPrivacy = view.findViewById(R.id.panelPrivacy);
                panelPrivacy.setVisibility(View.VISIBLE);

            }
        });
    }

}
