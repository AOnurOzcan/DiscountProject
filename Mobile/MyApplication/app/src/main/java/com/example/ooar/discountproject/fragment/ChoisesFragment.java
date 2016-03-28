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
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.UserActivity;
import com.example.ooar.discountproject.model.Category;
import com.example.ooar.discountproject.model.CompanyCategory;
import com.example.ooar.discountproject.model.Preference;
import com.example.ooar.discountproject.util.FragmentChangeListener;
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
        List<LinearLayout> linearLayoutList = new ArrayList<LinearLayout>();
        List<Button> buttonList = new ArrayList<Button>();
        List<Button> editCompanyList = new ArrayList<>();

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

            LinearLayout mainLayout = (LinearLayout) custom.findViewById(R.id.mainLayout);//root layout dan main layout alınıyor
            mainLayout.setTag(category.getId());
            linearLayoutList.add(mainLayout);


            Button button = (Button) custom.findViewById(R.id.parentCategory);//Parent kategori butonları alınıp yenı degerler set ediliyor
            button.setText(category.getCategoryName());
            button.setTag(category.getId());
            buttonList.add(button);

            boolean subCategorySizeBigZero = false;
            for (Category subCategory : childCategoryList) { //alt kategoriler için checkbox oluşturuluyor
                if (subCategory.getParentCategory() == category.getId()) {
                    subCategorySizeBigZero = true;
                    View custom2 = inflater.inflate(R.layout.preference_checkbox_layout, null);

                    LinearLayout subCategoryLayout = (LinearLayout) custom2.findViewById(R.id.subCategoryLayout);
                    ((ViewGroup) subCategoryLayout.getParent()).removeView(subCategoryLayout);

                    CheckBox subCheckbox = (CheckBox) subCategoryLayout.findViewById(R.id.subCategory); //checkbox layoutundan checkbox alınıyor ve değerleri set ediliyor
                    subCheckbox.setText(subCategory.getCategoryName());
                    subCheckbox.setTag(subCategory.getId());

                    Button editCompanyButton = (Button) subCategoryLayout.findViewById(R.id.editCompanyButton);
                    editCompanyButton.setTag(subCategory.getId());
                    editCompanyList.add(editCompanyButton);

                    for (Preference preference : preferencesList) {
                        if (subCheckbox.getTag().equals(preference.getCategoryId().getId())) {
                            subCheckbox.setChecked(true);
                            editCompanyButton.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                    checkboxList.add(subCheckbox);
                    checkboxLayout.addView(subCategoryLayout);
                }
            }

            if (!subCategorySizeBigZero) {
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
                        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                        fc.replaceFragment(UserActivity.userTabsFragment, null);
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
                        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                        fc.replaceFragment(UserActivity.userTabsFragment, null);
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
                List<CompanyCategory> newAddingList = new ArrayList<>();
                List<Preference> deletingList = new ArrayList<>();
                boolean addList;
                for (Preference preference : preferencesList) {
                    addList = true;
                    for (CompanyCategory companyCategory : selectedCompanyList) {
                        if (preference.getCategoryId().getId() == companyCategory.getCategoryId().getId() && preference.getCompanyId().getId() == companyCategory.getCompanyId().getId()) {
                            addList = false;
                            break;
                        } else {
                            addList = true;
                        }
                    }
                    if (addList) {
                        deletingList.add(preference);
                    }
                }
                for (CompanyCategory companyCategory : selectedCompanyList) {
                    addList = true;
                    for (Preference preference : preferencesList) {
                        if (preference.getCategoryId().getId() == companyCategory.getCategoryId().getId() && preference.getCompanyId().getId() == companyCategory.getCompanyId().getId()) {
                            addList = false;
                            break;
                        } else {
                            addList = true;
                        }
                    }
                    if (addList) {
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

        setEvents(savePreferences, checkboxList, buttonList, linearLayoutList, editCompanyList);
    }

    public void setEvents(final Button savePreferences, List<CheckBox> checkBoxList, List<Button> buttonList, final List<LinearLayout> linearLayoutList, final List<Button> editCompanyButtonList) {

        final List<CheckBox> companyCategoryCheckedList = new ArrayList<>();
        final List<CheckBox> tempCompanyCategoryCheckedList = new ArrayList<>();

        for (Preference preference : preferencesList) {
            for (CompanyCategory category : companyList) {
                if (category.getCategoryId().getId() == preference.getCategoryId().getId() && category.getCompanyId().getId() == preference.getCompanyId().getId()) {
                    selectedCompanyList.add(category);
                }
            }
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

        for (final Button button : editCompanyButtonList) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createBuilder(null, button, companyCategoryCheckedList, tempCompanyCategoryCheckedList, savePreferences);
                }
            });
        }

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

                        Button tempButton = (Button) ((ViewGroup) checkBox.getParent()).findViewById(R.id.editCompanyButton);
                        tempButton.setVisibility(View.GONE);
                    } else {
                        createBuilder(checkBox, null, companyCategoryCheckedList, tempCompanyCategoryCheckedList, savePreferences);
                    }
                }
            });
        }
    }

    public void createBuilder(final CheckBox checkBox, final Button button, final List<CheckBox> companyCategoryCheckedList, final List<CheckBox> tempCompanyCategoryCheckedList, final Button savePreferences) {
        boolean showDialog = false;
        List<Integer> companyIdList = new ArrayList<Integer>();
        final List<CheckBox> companyCheckbox = new ArrayList<CheckBox>();
        int categoryId;
        if (button == null) {
            categoryId = (int) checkBox.getTag();
        } else {
            categoryId = (int) button.getTag();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Firmalarınızı Seçiniz");
        builder.setCancelable(false);
        builder.create().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LinearLayout newLayout = new LinearLayout(getActivity());
        newLayout.setOrientation(LinearLayout.VERTICAL);

        for (CompanyCategory companyCategory : companyList) {
            if (categoryId == companyCategory.getCategoryId().getId()) {
                showDialog = true;
                final CheckBox companyCheckBox = Util.createCheckbox(getActivity(), companyCategory.getId(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), companyCategory.getCompanyId().getCompanyName());
                if (Util.companyCategoryFindId(selectedCompanyList, companyCategory.getId()) != -1) {
                    companyCheckBox.setChecked(true);
                    int index = Util.findIndexForCheckboxList(companyCategoryCheckedList, companyCheckBox);
                    if (index == -1) {
                        companyCategoryCheckedList.add(companyCheckBox);
                        tempCompanyCategoryCheckedList.add(companyCheckBox);
                    }

                }
                companyCheckbox.add(companyCheckBox);
                companyIdList.add(companyCategory.getCompanyId().getId());

                companyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (companyCheckBox.isChecked() && Util.findIndexForCheckboxList(companyCategoryCheckedList, companyCheckBox) == -1) {
                            companyCategoryCheckedList.add(companyCheckBox);
                        } else if (!companyCheckBox.isChecked()) {
                            int removeCheckboxIndex = Util.findIndexForCheckboxList(companyCategoryCheckedList, companyCheckBox);
                            if (removeCheckboxIndex != -1) {
                                companyCategoryCheckedList.remove(removeCheckboxIndex);
                                int removeCompanyCategoryIndex = Util.companyCategoryFindId(selectedCompanyList, (Integer) companyCheckBox.getTag());
                                if (removeCompanyCategoryIndex != -1) {
                                    selectedCompanyList.remove(removeCompanyCategoryIndex);
                                }
                            }
                        }
                    }
                });
                newLayout.addView(companyCheckBox);

            }
        }

        builder.setPositiveButton("Firmaları Kaydet", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                tempCompanyCategoryCheckedList.removeAll(tempCompanyCategoryCheckedList);
                for (CheckBox companyCheckbox : companyCategoryCheckedList) {
                    tempCompanyCategoryCheckedList.add(companyCheckbox);
                    for (CompanyCategory companyCategory : companyList) {
                        if (companyCheckbox.getTag().equals(companyCategory.getId()) && selectedCompanyList.indexOf(companyCategory) == -1) {
                            selectedCompanyList.add(companyCategory);
                        }
                    }
                }

                boolean selectedCompany = false;
                for (CheckBox tempCompanyCheckbox : companyCheckbox) {
                    if (tempCompanyCheckbox.isChecked()) {
                        if (button == null) {
                            Button tempButton = (Button) ((ViewGroup) checkBox.getParent()).findViewById(R.id.editCompanyButton);
                            tempButton.setVisibility(View.VISIBLE);
                        } else {
                            button.setVisibility(View.VISIBLE);
                        }
                        selectedCompany = true;
                        break;
                    }
                }

                if (!selectedCompany) {
                    if (button == null) {
                        checkBox.setChecked(false);
                    } else {
                        CheckBox tempCheckBox = (CheckBox) ((ViewGroup) button.getParent()).findViewById(R.id.subCategory);
                        tempCheckBox.setChecked(false);
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
                for (CheckBox tempCheckBox : tempCompanyCategoryCheckedList) {
                    companyCategoryCheckedList.add(tempCheckBox);
                    for (CompanyCategory companyCategory : companyList) {
                        if (tempCheckBox.getTag().equals(companyCategory.getId()) && selectedCompanyList.indexOf(companyCategory) == -1) {
                            selectedCompanyList.add(companyCategory);
                        }
                    }
                }

                if (button == null) {
                    boolean selectedCompany = false;
                    for (CheckBox tempCompanyCheckbox : companyCheckbox) {
                        if (tempCompanyCheckbox.isChecked() && Util.findIndexForCheckboxList(tempCompanyCategoryCheckedList, tempCompanyCheckbox) != -1) {
                            Button button = (Button) ((ViewGroup) checkBox.getParent()).findViewById(R.id.editCompanyButton);
                            button.setVisibility(View.VISIBLE);
                            selectedCompany = true;
                            break;
                        }
                    }

                    if (!selectedCompany) {
                        checkBox.setChecked(false);
                    }
                }

                if (!Util.isEqual(preferencesList, selectedCompanyList)) {
                    savePreferences.setEnabled(true);
                } else {
                    savePreferences.setEnabled(false);
                }
            }
        });

        if (showDialog) {
            builder.setView(newLayout).show();
        } else {
            if (button == null) {
                checkBox.setChecked(false);
            }
            Toast.makeText(getActivity(), "Gösterilecek Firma Yok", Toast.LENGTH_SHORT).show();
        }
    }
}
