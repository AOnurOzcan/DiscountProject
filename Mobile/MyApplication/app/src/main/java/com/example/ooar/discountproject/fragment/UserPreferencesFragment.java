package com.example.ooar.discountproject.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.model.Category;
import com.example.ooar.discountproject.model.CompanyCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Onur Kuru on 7.3.2016.
 */
public class UserPreferencesFragment extends Fragment {

    public static List<Category> categoryList;
    public static List<CompanyCategory> companyList;

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
        List<CheckBox> checkboxList = new ArrayList<CheckBox>();
        List<CheckBox> allSelectCheckBox = new ArrayList<>();
        List<LinearLayout> linearLayoutList = new ArrayList<LinearLayout>();
        List<Button> buttonList = new ArrayList<Button>();
        List<Button> selectCompanyButtonList = new ArrayList<Button>();

        for (Category category : categoryList) {//ana kategoriler bir listede toplanıyor
            if (category.getParentCategory() == null) {
                parentCategoryList.add(category);
            } else {
                childCategoryList.add(category); //alt kategoriler bir listede toplanıyor
            }
        }
        for (Category category : parentCategoryList) {

            Button button = new Button(getActivity());//Ana kategori butonu oluşturuluyor
            button.setText(category.getCategoryName());
            button.setLayoutParams(params);
            button.setId(category.getId());
            buttonList.add(button);
            rootLayout.addView(button);

            LinearLayout newLayout = new LinearLayout(getActivity());
            newLayout.setOrientation(LinearLayout.VERTICAL);
            newLayout.setVisibility(View.GONE);
            newLayout.setId(category.getId());
            linearLayoutList.add(newLayout);

            CheckBox checkBox = new CheckBox(getActivity());//Hepsini seç checkbox'ı oluşturuluyor
            checkBox.setText("Hepsini Seç");
            checkBox.setId(category.getId());
            checkBox.setLayoutParams(params);
            allSelectCheckBox.add(checkBox);

            newLayout.addView(checkBox);

            for (Category subCategory : childCategoryList) { //alt kategoriler için checkbox oluşturuluyor
                if (subCategory.getParentCategory().getId() == category.getId()) {
                    checkBox = new CheckBox(getActivity());
                    checkBox.setText(subCategory.getCategoryName());
                    checkBox.setLayoutParams(params);
                    checkBox.setId(subCategory.getId());
                    checkboxList.add(checkBox);
                    newLayout.addView(checkBox);
                }
            }
            button = new Button(getActivity());//Firma seç butonu
            button.setText("Firma Seç");
            button.setLayoutParams(params);
            button.setId(category.getId());
            selectCompanyButtonList.add(button);
            newLayout.addView(button);

            rootLayout.addView(newLayout);
        }

        setEvents(buttonList, allSelectCheckBox, linearLayoutList, selectCompanyButtonList);
    }

    public void setEvents(List<Button> buttonList, List<CheckBox> allSelectCheckBox, final List<LinearLayout> linearLayoutList, List<Button> selectCompanyButtonList) {

        for (final Button button : buttonList) {//Ana kategori butonları için event oluşturuluyor
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
        }

        for (final CheckBox checkBox : allSelectCheckBox) {//hepsini seçmek için event oluşturuluyor
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for (LinearLayout linearLayout : linearLayoutList) {
                        if (linearLayout.getId() == checkBox.getId()) {
                            if (checkBox.isChecked()) {
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
        }

        for (final Button button : selectCompanyButtonList) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Integer> categoryIdList = new ArrayList<Integer>();
                    List<Integer> companyIdList = new ArrayList<Integer>();
                    for (LinearLayout linearLayout : linearLayoutList) {
                        if (linearLayout.getId() == button.getId()) {
                            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                                try {
                                    CheckBox checkBoxItem = (CheckBox) linearLayout.getChildAt(i);
                                    if (checkBoxItem.isChecked()) {
                                        categoryIdList.add(checkBoxItem.getId());
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Firmalarınızı Seçiniz");

                    LinearLayout newLayout = new LinearLayout(getActivity());
                    newLayout.setOrientation(LinearLayout.VERTICAL);

                    for (int categoryId : categoryIdList) {
                        for (CompanyCategory companyCategory : companyList) {
                            if (categoryId == companyCategory.getCategoryId().getId() && companyIdList.indexOf(companyCategory.getCompanyId().getId()) == -1) {
                                companyIdList.add(companyCategory.getCompanyId().getId());

                                CheckBox checkBox = new CheckBox(getActivity());
                                checkBox.setId(companyCategory.getId());
                                checkBox.setText(companyCategory.getCompanyId().getCompanyName());
                                checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                newLayout.addView(checkBox);
                            }
                        }
                    }

                    builder.setView(newLayout).show();
                }
            });
        }
    }
}
