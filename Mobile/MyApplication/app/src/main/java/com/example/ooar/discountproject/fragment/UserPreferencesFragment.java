package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.UserActivity;
import com.example.ooar.discountproject.model.Category;
import com.example.ooar.discountproject.model.CompanyCategory;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Onur Kuru on 7.3.2016.
 */
public class UserPreferencesFragment extends Fragment {

    public static List<Category> categoryList;
    public static List<CompanyCategory> companyList;
    public List<CompanyCategory> selectedCompanyList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.preference_layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        renderLayout(view);
    }

    public void renderLayout(View view) {

        List<Category> parentCategoryList = new ArrayList<Category>();
        List<Category> childCategoryList = new ArrayList<Category>();
        List<CheckBox> checkboxList = new ArrayList<CheckBox>();
        List<CheckBox> allSelectCheckBox = new ArrayList<>();
        List<LinearLayout> linearLayoutList = new ArrayList<LinearLayout>();
        List<LinearLayout> allSelectLinearLayoutList = new ArrayList<LinearLayout>();
        List<Button> buttonList = new ArrayList<Button>();
        List<Button> selectCompanyButtonList = new ArrayList<Button>();


        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.preferenceScrollView); //preferences root layout scrollview alınıyor
        LinearLayout rootLayout = (LinearLayout) scrollView.findViewById(R.id.preferenceRootLayout); //preferences root layout ana layout alınıyor
        Button savePreferences = (Button) scrollView.findViewById(R.id.savePreferences); //tercihleri kaydetme butonu alınıyor
        rootLayout.removeAllViews();

        for (Category category : categoryList) {//ana kategoriler bir listede toplanıyor
            if (category.getParentCategory() == null) {
                parentCategoryList.add(category);
            } else {
                childCategoryList.add(category); //alt kategoriler bir listede toplanıyor
            }
        }

        for (Category category : parentCategoryList) {
            View custom = inflater.inflate(R.layout.preference_root_layout, null);
            LinearLayout checkboxLayout = (LinearLayout) custom.findViewById(R.id.checkboxLayout);//root layout dan checkboxların toplanacagı layout alınıyor
            checkboxLayout.setTag(category.getId());
            allSelectLinearLayoutList.add(checkboxLayout);

            LinearLayout mainLayout = (LinearLayout) custom.findViewById(R.id.mainLayout);//root layout dan main layout alınıyor
            mainLayout.setTag(category.getId());
            linearLayoutList.add(mainLayout);


            Button button = (Button) custom.findViewById(R.id.parentCategory);//Parent kategori butonları alınıp yenı degerler set ediliyor
            button.setTag(category.getId());
            button.setText(category.getCategoryName());
            buttonList.add(button);

            CheckBox checkBox = (CheckBox) checkboxLayout.findViewById(R.id.selectAll);//hepsini seç checkbox ı alınıyor
            checkBox.setTag(category.getId());
            allSelectCheckBox.add(checkBox);

            boolean subCategorySizeBigZero = false;
            for (Category subCategory : childCategoryList) { //alt kategoriler için checkbox oluşturuluyor
                if (subCategory.getParentCategory() == category.getId()) {
                    subCategorySizeBigZero = true;
                    View custom2 = inflater.inflate(R.layout.preference_checkbox_layout, null);

                    CheckBox subCheckbox = (CheckBox) custom2.findViewById(R.id.subCategory); //checkbox layoutundan checkbox alınıyor ve değerleri set ediliyor
                    ((ViewGroup) subCheckbox.getParent()).removeView(subCheckbox);
                    subCheckbox.setText(subCategory.getCategoryName());
                    subCheckbox.setTag(subCategory.getId());
                    checkboxList.add(subCheckbox);

                    checkboxLayout.addView(subCheckbox); //checkboxların toplandığı layouta gömülüyor
                }
            }

            Button selectBranch = (Button) custom.findViewById(R.id.selectBranch);//firmaları kaydetme butonu alınıyor ve set ediliyor
            selectBranch.setTag(category.getId());
            selectCompanyButtonList.add(selectBranch);

            if (!subCategorySizeBigZero) {
                selectBranch.setVisibility(View.GONE);
                checkBox.setVisibility(View.GONE);
                custom.findViewById(R.id.helpBlockLayout).setVisibility(View.VISIBLE);
            }

            rootLayout.addView(custom);//oluşturulan tüm layoutlar ana layouta gomuluyor
        }

        savePreferences.setOnClickListener(new View.OnClickListener() {

            final Callback callback = new Callback() {
                @Override
                public void success(Object o, Response response) {
                    Util.stopProgressDialog();
                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    getActivity().startActivity(intent);
                }

                @Override
                public void failure(RetrofitError error) {
                    Util.stopProgressDialog();
                    Toast.makeText(getActivity(), "Sunucudan Yanıt Alınamadı", Toast.LENGTH_LONG).show();
                }
            };

            @Override
            public void onClick(View v) {

                String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
                Util.startProgressDialog();
                RetrofitConfiguration.getRetrofitService().createUserPreferences(tokenKey, selectedCompanyList, callback);
            }
        });
        rootLayout.addView(savePreferences);

        setEvents(savePreferences, checkboxList, buttonList, allSelectCheckBox, linearLayoutList, selectCompanyButtonList, allSelectLinearLayoutList);//eklenen tüm buton ve checkboxlara evenlar atanıyor
    }

    public void setEvents(final Button savePreferences, List<CheckBox> checkBoxList, List<Button> buttonList, List<CheckBox> allSelectCheckBox, final List<LinearLayout> linearLayoutList, final List<Button> selectCompanyButtonList, final List<LinearLayout> allSelectLinearLayoutList) {

        for (final CheckBox checkBox : checkBoxList) {//Kullanıcı takip ettiği bir kategoriyi kaldırırsa ona ve bu kategori selectedCompany listesinde bulunuyosa listeden kaldırılır.
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        for (CompanyCategory companyCategory : selectedCompanyList) {
                            if (checkBox.getTag().equals(companyCategory.getCategoryId().getId())) {
                                selectedCompanyList.remove(companyCategory);
                            }
                        }
                        if (selectedCompanyList.size() > 0) {
                            savePreferences.setEnabled(true);
                        } else {
                            savePreferences.setEnabled(false);
                        }
                    } else {
                        boolean indexOf = false;
                        for (CompanyCategory companyCategory : companyList) {
                            if (checkBox.getTag().equals(companyCategory.getCategoryId().getId())) {
                                indexOf = true;
                                break;
                            } else {
                                indexOf = false;
                            }
                        }

                        if (!indexOf) {
                            Toast.makeText(getActivity(), "Bu Kategoride Seçilebilecek Bir Firma Yok", Toast.LENGTH_LONG).show();
                            checkBox.setChecked(false);
                        }
                    }
                }
            });
        }

        for (final Button button : buttonList) {//Ana kategori butonları için event oluşturuluyor. adssda
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (LinearLayout linearLayout : linearLayoutList) {
                        if (linearLayout.getTag().equals(button.getTag())) {
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
                    boolean checked = false;
                    for (LinearLayout linearLayout : allSelectLinearLayoutList) {
                        if (linearLayout.getTag().equals(checkBox.getTag())) {
                            if (checkBox.isChecked()) {
                                checked = true;
                            } else {
                                checked = false;
                            }
                            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                                try {
                                    CheckBox checkBoxItem = (CheckBox) linearLayout.getChildAt(i);
                                    checkBoxItem.setChecked(checked);
                                } catch (Exception ignored) {
                                }
                            }
                            break;
                        }
                    }
                }
            });
        }

        for (final Button button : selectCompanyButtonList) {//firma seçmek için event oluşturuluyor
            final List<CheckBox> companyCategoryCheckedList = new ArrayList<>();
            final List<CheckBox> tempCompanyCategoryCheckedList = new ArrayList<>();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean showDialog = false;
                    List<Integer> categoryIdList = new ArrayList<Integer>();
                    final List<Integer> companyIdList = new ArrayList<Integer>();

                    for (LinearLayout linearLayout : allSelectLinearLayoutList) {//seçili olan kategorileri bulmak için
                        if (linearLayout.getTag().equals(button.getTag())) {
                            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                                try {
                                    CheckBox checkBoxItem = (CheckBox) linearLayout.getChildAt(i);
                                    if (checkBoxItem.isChecked()) {
                                        categoryIdList.add((Integer) checkBoxItem.getTag());
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Firmalarınızı Seçiniz");
                    builder.create().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                    final LinearLayout newLayout = new LinearLayout(getActivity());
                    newLayout.setOrientation(LinearLayout.VERTICAL);

                    for (final int categoryId : categoryIdList) {
                        for (final CompanyCategory companyCategory : companyList) {
                            if (categoryId == companyCategory.getCategoryId().getId()) {
                                showDialog = true;
                                final CheckBox checkBox = Util.createCheckbox(getActivity(), companyCategory.getId(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), companyCategory.getCompanyId().getCompanyName());
                                if (companyIdList.indexOf(companyCategory.getCompanyId().getId()) != -1) {
                                    checkBox.setVisibility(View.GONE);
                                }
                                if (Util.companyCategoryFindId(selectedCompanyList, companyCategory.getId()) != -1) {
                                    checkBox.setChecked(true);
                                }
                                companyIdList.add(companyCategory.getCompanyId().getId());

                                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (checkBox.isChecked() && Util.findIndexForCheckboxList(companyCategoryCheckedList, checkBox) == -1) {
                                            for (int i = 0; i < newLayout.getChildCount(); i++) {
                                                try {
                                                    CheckBox checkBoxItem = (CheckBox) newLayout.getChildAt(i);
                                                    if (String.valueOf(checkBoxItem.getText()).equals(String.valueOf(checkBox.getText()))) {
                                                        companyCategoryCheckedList.add(checkBoxItem);
                                                    }
                                                } catch (Exception ignored) {
                                                }
                                            }
                                        } else if (!checkBox.isChecked()) {
                                            for (int i = 0; i < newLayout.getChildCount(); i++) {
                                                try {
                                                    CheckBox checkBoxItem = (CheckBox) newLayout.getChildAt(i);
                                                    if (String.valueOf(checkBoxItem.getText()).equals(String.valueOf(checkBox.getText()))) {
                                                        int removeCheckboxIndex = Util.findIndexForCheckboxList(companyCategoryCheckedList, checkBoxItem);
                                                        if (removeCheckboxIndex != -1) {
                                                            companyCategoryCheckedList.remove(removeCheckboxIndex);
                                                            int removeCompanyCategoryIndex = Util.companyCategoryFindId(selectedCompanyList, (Integer) checkBoxItem.getTag());
                                                            if (removeCompanyCategoryIndex != -1) {
                                                                selectedCompanyList.remove(removeCompanyCategoryIndex);
                                                            }
                                                        }
                                                    }
                                                } catch (Exception ignored) {
                                                }
                                            }
                                        }
                                    }
                                });
                                newLayout.addView(checkBox);
                            }
                        }
                    }

                    builder.setPositiveButton("Firmaları Kaydet", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            tempCompanyCategoryCheckedList.removeAll(tempCompanyCategoryCheckedList);
                            for (CheckBox checkbox : companyCategoryCheckedList) {
                                tempCompanyCategoryCheckedList.add(checkbox);
                                for (CompanyCategory companyCategory : companyList) {
                                    if (checkbox.getTag().equals(companyCategory.getId()) && selectedCompanyList.indexOf(companyCategory) == -1) {
                                        selectedCompanyList.add(companyCategory);
                                    }
                                }
                            }

                            if (selectedCompanyList.size() > 0) {
                                savePreferences.setEnabled(true);
                            } else {
                                savePreferences.setEnabled(false);
                            }
                        }
                    }).setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            companyCategoryCheckedList.removeAll(companyCategoryCheckedList);
                            for (CheckBox checkBox : tempCompanyCategoryCheckedList) {
                                companyCategoryCheckedList.add(checkBox);
                                for (CompanyCategory companyCategory : companyList) {
                                    if (checkBox.getTag().equals(companyCategory.getId()) && selectedCompanyList.indexOf(companyCategory) == -1) {
                                        selectedCompanyList.add(companyCategory);
                                    }
                                }
                            }
                            if (selectedCompanyList.size() > 0) {
                                savePreferences.setEnabled(true);
                            } else {
                                savePreferences.setEnabled(false);
                            }
                            dialog.cancel();
                        }
                    });
                    if (showDialog) {
                        builder.setView(newLayout).show();
                    } else {
                        Toast.makeText(getActivity(), "Gösterilecek Firma Yok", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }
}
