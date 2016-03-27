package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.UserActivity;
import com.example.ooar.discountproject.model.Category;
import com.example.ooar.discountproject.model.Company;
import com.example.ooar.discountproject.model.CompanyCategory;
import com.example.ooar.discountproject.model.Preference;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Ramos on 06.03.2016.
 */
public class ChoisesFragment extends Fragment {
    public static List<Category> categoryList = null;
    public static List<CompanyCategory> companyList = null;
    public static List<Preference> preferencesList = null;
    public List<CompanyCategory> selectedCompanyList = new ArrayList<>();
    public boolean deleteResponse = false;
    public boolean createResponse = false;
    public boolean cache = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.preference_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (categoryList == null || companyList == null || preferencesList == null) {
            getAllCategories();
        } else {
            this.onViewStateRestored(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cache) {
            selectedCompanyList.clear();
            renderPage();
        }
        cache = true;
    }

    public void getAllCategories() {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                categoryList = (List<Category>) o;
                getAllCompany();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitConfiguration.getRetrofitService(true).getAllCategories(callback);
    }

    public void getAllCompany() {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                companyList = (List<CompanyCategory>) o;
                getUserPreferences();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitConfiguration.getRetrofitService(true).getAllCompanyWithCategory(callback);
    }

    public void getUserPreferences() {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                preferencesList = (List<Preference>) o;
                renderPage();
                Util.stopProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };
        String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
        RetrofitConfiguration.getRetrofitService(true).getUserPreferences(tokenKey, callback);
    }

    public void renderPage() {
        List<Category> parentCategoryList = new ArrayList<Category>();
        List<Category> childCategoryList = new ArrayList<Category>();
        List<CheckBox> checkboxList = new ArrayList<CheckBox>();
        List<CheckBox> allSelectCheckBox = new ArrayList<>();
        List<LinearLayout> allSelectLinearLayoutList = new ArrayList<LinearLayout>();
        List<LinearLayout> linearLayoutList = new ArrayList<LinearLayout>();
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
            button.setText(category.getCategoryName());
            button.setTag(category.getId());
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

                    for (Preference preference : preferencesList) {
                        if (subCheckbox.getTag().equals(preference.getCategoryId().getId())) {
                            subCheckbox.setChecked(true);
                            break;
                        }
                    }
                    checkboxList.add(subCheckbox);
                    checkboxLayout.addView(subCheckbox);
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

            rootLayout.addView(custom);
        }

        savePreferences.setOnClickListener(new View.OnClickListener() {

            final Callback createCallback = new Callback() {
                @Override
                public void success(Object o, Response response) {
                    createResponse = true;
                    if (deleteResponse) {
                        categoryList = null;
                        companyList = null;
                        preferencesList = null;
                        cache = false;
                        selectedCompanyList.clear();
                        Intent intent = new Intent(getActivity(), UserActivity.class);
                        getActivity().startActivity(intent);
                        Util.stopProgressDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                }
            };
            final Callback deleteCallback = new Callback() {
                @Override
                public void success(Object o, Response response) {
                    deleteResponse = true;
                    if (createResponse) {
                        categoryList = null;
                        companyList = null;
                        preferencesList = null;
                        cache = false;
                        selectedCompanyList.clear();
                        Intent intent = new Intent(getActivity(), UserActivity.class);
                        getActivity().startActivity(intent);
                        Util.stopProgressDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                }
            };

            @Override
            public void onClick(View v) {
                String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
//                ProgressDialog.show(getActivity(), "", "İşleminiz Yürütülüyor. Lütfen bekleyin.");
                List<CompanyCategory> newList = new ArrayList<>();
                List<CompanyCategory> newAddingList = new ArrayList<>();
                List<Preference> deletingList = new ArrayList<>();
                boolean addNewList;
                for (Preference preference : preferencesList) {
                    addNewList = true;
                    for (CompanyCategory companyCategory : selectedCompanyList) {
                        if (preference.getCategoryId().getId() == companyCategory.getCategoryId().getId() && preference.getCompanyId().getId() == companyCategory.getCompanyId().getId()) {
                            addNewList = false;
                            break;
                        } else {
                            addNewList = true;
                        }
                    }
                    if (addNewList) {
                        deletingList.add(preference);
                    }
                }
                for (CompanyCategory companyCategory : selectedCompanyList) {
                    addNewList = true;
                    for (Preference preference : preferencesList) {
                        if (preference.getCategoryId().getId() == companyCategory.getCategoryId().getId() && preference.getCompanyId().getId() == companyCategory.getCompanyId().getId()) {
                            addNewList = false;
                            break;
                        } else {
                            addNewList = true;
                        }
                    }
                    if (addNewList) {
                        newAddingList.add(companyCategory);
                    }
                }
                if (deletingList.size() == 0) {
                    deleteResponse = true;
                }
                if (newAddingList.size() == 0) {
                    createResponse = true;
                }

                if (deletingList.size() != 0) {
                    RetrofitConfiguration.getRetrofitService(true).deleteUserPreferences(tokenKey, deletingList, deleteCallback);
                }
                if (newAddingList.size() != 0) {
                    RetrofitConfiguration.getRetrofitService(true).createUserPreferences(tokenKey, newAddingList, createCallback);
                }
            }
        });
        rootLayout.addView(savePreferences);

        setEvents(savePreferences, checkboxList, buttonList, allSelectCheckBox, linearLayoutList, selectCompanyButtonList, allSelectLinearLayoutList);
    }

    public void setEvents(final Button savePreferences, List<CheckBox> checkBoxList, List<Button> buttonList, List<CheckBox> allSelectCheckBox, final List<LinearLayout> linearLayoutList, final List<Button> selectCompanyButtonList, final List<LinearLayout> allSelectLinearLayoutList) {

        for (final CheckBox checkBox : checkBoxList) {//Kullanıcı takip ettiği bir kategoriyi kaldırırsa ona ve bu kategori selectedCompany listesinde bulunuyosa listeden kaldırılır.
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        Iterator<CompanyCategory> iterator = selectedCompanyList.iterator();
                        while (iterator.hasNext()) {
                            CompanyCategory companyCategory = iterator.next();
                            if (checkBox.getTag().equals(companyCategory.getCategoryId().getId())) {
                                iterator.remove();
                            }
                        }

                        if (!Util.isEqual(preferencesList, selectedCompanyList)) {
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
        for (Preference preference : preferencesList) {
            for (CompanyCategory category : companyList) {
                if (category.getCategoryId().getId() == preference.getCategoryId().getId() && category.getCompanyId().getId() == preference.getCompanyId().getId()) {
                    selectedCompanyList.add(category);
                }
            }
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
                                    int removeCheckboxIndex = Util.findIndexForCheckboxList(companyCategoryCheckedList, checkBox);
                                    if (removeCheckboxIndex == -1) {
                                        companyCategoryCheckedList.add(checkBox);
                                        tempCompanyCategoryCheckedList.add(checkBox);
                                    }
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
                            if (!Util.isEqual(preferencesList, selectedCompanyList)) {
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
                            if (!Util.isEqual(preferencesList, selectedCompanyList)) {
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
                        Toast.makeText(getActivity(), "Gösterilecek Firma Yok", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }
}
