package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.model.Category;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.RetrofitConfiguration;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Ramos on 06.03.2016.
 */
public class ChoisesFragment extends Fragment {
    private static List<Category> categoryList;
    List<LinearLayout> linearLayoutList = new ArrayList<LinearLayout>();

    public void getAllCategories() {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                ChoisesFragment.categoryList = (List<Category>) o;
                renderPage();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitConfiguration.getRetrofitService().getAllCategories(callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choises, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        getAllCategories();


//        Button btnProfile = (Button) view.findViewById(R.id.btnProfile);
//        Button btnSettings = (Button) view.findViewById(R.id.btnSettings);
//        Button btnPrivacy = (Button) view.findViewById(R.id.btnPrivacy);
//
//        View panelProfile = view.findViewById(R.id.panelProfile);
//        panelProfile.setVisibility(View.GONE);
//
//        View panelSettings = view.findViewById(R.id.panelSettings);
//        panelSettings.setVisibility(View.GONE);
//
//        View panelPrivacy = view.findViewById(R.id.panelPrivacy);
//        panelPrivacy.setVisibility(View.GONE);

//        btnProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // DO STUFF
//                View panelProfile = view.findViewById(R.id.panelProfile);
//                panelProfile.setVisibility(View.VISIBLE);
//
//                View panelSettings = view.findViewById(R.id.panelSettings);
//                panelSettings.setVisibility(View.GONE);
//
//                View panelPrivacy = view.findViewById(R.id.panelPrivacy);
//                panelPrivacy.setVisibility(View.GONE);
//
//            }
//        });
//
//        btnSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // DO STUFF
//                View panelProfile = view.findViewById(R.id.panelProfile);
//                panelProfile.setVisibility(View.GONE);
//
//                View panelSettings = view.findViewById(R.id.panelSettings);
//                panelSettings.setVisibility(View.VISIBLE);
//
//                View panelPrivacy = view.findViewById(R.id.panelPrivacy);
//                panelPrivacy.setVisibility(View.GONE);
//
//            }
//        });

//        btnPrivacy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // DO STUFF
//                View panelProfile = view.findViewById(R.id.panelProfile);
//                panelProfile.setVisibility(View.GONE);
//
//                View panelSettings = view.findViewById(R.id.panelSettings);
//                panelSettings.setVisibility(View.GONE);
//
//                View panelPrivacy = view.findViewById(R.id.panelPrivacy);
//                panelPrivacy.setVisibility(View.VISIBLE);
//
//            }
//        });
    }

    public void renderPage() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout rootLayout = (LinearLayout) getView().findViewById(R.id.userChoisesRoot_Layout);
        for (Category categoryItem : categoryList) {
            final LinearLayout linearLayout = new LinearLayout(getActivity());
            if (categoryItem.getParentCategory() == null) {

                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setLayoutParams(params);

                linearLayout.setId(categoryItem.getId());
                linearLayoutList.add(linearLayout);
                final Button button = new Button(getActivity());
                button.setText(categoryItem.getCategoryName());
                button.setId(categoryItem.getId());
                button.setLayoutParams(params);
                rootLayout.addView(button);
                rootLayout.addView(linearLayout);

                final CheckBox chk = new CheckBox(getActivity());
                chk.setLayoutParams(params);
                chk.setId(categoryItem.getId());
                chk.setText("Hepsini Seç");
                linearLayout.addView(chk);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (LinearLayout linearLayout : linearLayoutList) {
                            if (linearLayout.getId() == button.getId()) {
                                linearLayout.setVisibility(View.VISIBLE);
                            } else {
                                linearLayout.setVisibility(View.GONE);
                            }
                        }
                    }
                });
                chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        for (LinearLayout linearLayout : linearLayoutList) {
                            if (linearLayout.getId() == chk.getId()) {
                                if (chk.isChecked()) {
                                    for (int i = 0; i < linearLayout.getChildCount(); i++) {
                                        try {
                                            CheckBox checkBoxItem = (CheckBox) linearLayout.getChildAt(i);
                                            checkBoxItem.setChecked(true);
                                        } catch (Exception ignored) {
                                        }
                                    }
                                } else {
                                    for (int i = 0; i < linearLayout.getChildCount(); i++) {
                                        try {
                                            CheckBox checkBoxItem = (CheckBox) linearLayout.getChildAt(i);
                                            checkBoxItem.setChecked(false);
                                        } catch (Exception ignored) {
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                });


            } else {
                CheckBox chk = new CheckBox(getActivity());
                chk.setId(categoryItem.getId());
                chk.setText(categoryItem.getCategoryName());
                chk.setLayoutParams(params);

                for (LinearLayout linearLayouts : linearLayoutList) {
                    if (linearLayouts.getId() == categoryItem.getParentCategory().getId()) {
                        linearLayouts.addView(chk);
                    }
                    else{

                    }
                }
            }
        }
        for (LinearLayout linearLayouts : linearLayoutList) {
            Button buttonCompany = new Button(getActivity());
            buttonCompany.setId(linearLayouts.getId());
            buttonCompany.setText("Firma Seç");
            buttonCompany.setLayoutParams(params);
            linearLayouts.addView(buttonCompany);
            buttonCompany.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Firma dialog açılacak
                }
            });
        }
    }
}
