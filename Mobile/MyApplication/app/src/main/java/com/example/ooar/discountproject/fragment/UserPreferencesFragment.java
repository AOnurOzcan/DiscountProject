package com.example.ooar.discountproject.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Onur Kuru on 7.3.2016.
 */
public class UserPreferencesFragment extends Fragment {

    public static List<Category> categoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_preferences, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        renderLayout(view);
    }

    public void renderLayout(View view) {
        LinearLayout rootLayout = (LinearLayout) view.findViewById(R.id.root_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        List<Category> parentCategoryList = new ArrayList<Category>();
        List<Category> childCategoryList = new ArrayList<Category>();
        List<CheckBox> checkboxIdList = new ArrayList<CheckBox>();
        final List<LinearLayout> layoutIdList = new ArrayList<LinearLayout>();
        List<Button> buttonIdList = new ArrayList<Button>();

        for (Category category : this.categoryList) {
            if (category.getParentCategory() == null) {
                parentCategoryList.add(category);
            } else {
                childCategoryList.add(category);
            }
        }

        for (Category category : parentCategoryList) {

            Button button = new Button(getActivity());
            button.setText(category.getCategoryName());
            button.setLayoutParams(params);
            button.setId(category.getId());
            buttonIdList.add(button);
            rootLayout.addView(button);

            LinearLayout newLayout = new LinearLayout(getActivity());
            newLayout.setOrientation(LinearLayout.VERTICAL);
            newLayout.setVisibility(View.GONE);
            newLayout.setId(category.getId());
            layoutIdList.add(newLayout);

            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setText("Hepsini Seç");
            checkBox.setId(category.getId());
            checkBox.setLayoutParams(params);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            checkboxIdList.add(checkBox);
            newLayout.addView(checkBox);

            for (Category category1 : childCategoryList) {
                if (category1.getParentCategory().getId() == category.getId()) {
                    checkBox = new CheckBox(getActivity());
                    checkBox.setText(category1.getCategoryName());
                    checkBox.setLayoutParams(params);
                    checkBox.setId(category1.getId());
                    checkboxIdList.add(checkBox);
                    newLayout.addView(checkBox);
                }
            }
            button = new Button(getActivity());
            button.setText("Firma Seç");
            button.setLayoutParams(params);
            button.setId(category.getId());
            buttonIdList.add(button);
            newLayout.addView(button);

            rootLayout.addView(newLayout);
        }
        for (final Button button : buttonIdList) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (LinearLayout linearLayout : layoutIdList) {
                        if (linearLayout.getId() == button.getId()) {
                            linearLayout.setVisibility(View.VISIBLE);
                        } else {
                            linearLayout.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }
}
