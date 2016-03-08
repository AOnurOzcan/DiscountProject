package com.example.ooar.discountproject.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.PasswordTransformationMethod;
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

    private List<Category> categoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Category parentCategory = new Category();
        Category category = new Category();

        categoryList = new ArrayList<Category>();
        parentCategory.setCategoryName("Teknoloji");
        parentCategory.setId(1);
        parentCategory.setParentCategory(null);
        categoryList.add(parentCategory);

        category = new Category();
        category.setCategoryName("Cep Telefonu");
        category.setParentCategory(parentCategory);
        category.setId(2);
        categoryList.add(category);

        category = new Category();
        category.setCategoryName("Bilgisayar");
        category.setParentCategory(parentCategory);
        category.setId(3);
        categoryList.add(category);

        category = new Category();
        category.setCategoryName("Tablet");
        category.setParentCategory(parentCategory);
        category.setId(4);
        categoryList.add(category);

        category = new Category();
        category.setCategoryName("Elektrikli ev aletleri");
        category.setParentCategory(parentCategory);
        category.setId(5);
        categoryList.add(category);

        parentCategory = new Category();

        parentCategory.setCategoryName("Gıda");
        parentCategory.setId(6);
        parentCategory.setParentCategory(null);
        categoryList.add(parentCategory);

        category = new Category();
        category.setCategoryName("Kırmızı Et");
        category.setParentCategory(parentCategory);
        category.setId(7);
        categoryList.add(category);

        category = new Category();
        category.setCategoryName("Sucuk");
        category.setParentCategory(parentCategory);
        category.setId(8);
        categoryList.add(category);

        category = new Category();
        category.setCategoryName("Salam");
        category.setParentCategory(parentCategory);
        category.setId(9);
        categoryList.add(category);

        category = new Category();
        category.setCategoryName("Peynir");
        category.setParentCategory(parentCategory);
        category.setId(10);
        categoryList.add(category);

        return inflater.inflate(R.layout.create_preferences, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

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
            checkBox.setLayoutParams(params);
            checkBox.setOnClickListener();
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
        for(final Button button: buttonIdList){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (LinearLayout linearLayout : layoutIdList){
                        if (linearLayout.getId() == button.getId()){
                            linearLayout.setVisibility(View.VISIBLE);
                        }else{
                            linearLayout.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }
}
