package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.UserActivity;
import com.example.ooar.discountproject.model.Category;
import com.example.ooar.discountproject.model.CompanyCategory;
import com.example.ooar.discountproject.model.Preference;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Ramos on 06.03.2016.
 */
public class ChoisesFragment extends Fragment {
    public static List<Category> categoryList = new ArrayList<>();
    public static List<CompanyCategory> companyList = new ArrayList<>();
    public static List<Preference> preferencesList = new ArrayList<>();
    public List<CompanyCategory> selectedCompanyList = new ArrayList<>();
    public void getAllCategories(final View v) {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                ChoisesFragment.categoryList = (List<Category>) o;
                getAllCompany(v);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitConfiguration.getRetrofitService(true).getAllCategories(callback);
    }

    public void getAllCompany(final View v) {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                ChoisesFragment.companyList = (List<CompanyCategory>) o;
                getUserPreferences(v);

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitConfiguration.getRetrofitService(true).getAllCompanyWithCategory(callback);
    }

    public void getUserPreferences(final View v) {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                ChoisesFragment.preferencesList = (List<Preference>) o;
                renderPage(v);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };
        String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
        RetrofitConfiguration.getRetrofitService(true).getUserPreferences(tokenKey, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choises, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        getAllCategories(view);
    }

    public void renderPage(final View v) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout rootLayout = (LinearLayout) getView().findViewById(R.id.userChoisesRoot_Layout);
        List<Category> parentCategoryList = new ArrayList<Category>();
        List<Category> childCategoryList = new ArrayList<Category>();
        List<CheckBox> checkboxList = new ArrayList<CheckBox>();
        List<CheckBox> allSelectCheckBox = new ArrayList<>();
        List<LinearLayout> linearLayoutList = new ArrayList<LinearLayout>();
        List<Button> buttonList = new ArrayList<Button>();
        List<Button> selectCompanyButtonList = new ArrayList<Button>();
        Button savePreferences;
        for (Category category : categoryList) {//ana kategoriler bir listede toplanıyor
            if (category.getParentCategory() == null) {
                parentCategoryList.add(category);
            } else {
                childCategoryList.add(category); //alt kategoriler bir listede toplanıyor
            }
        }
        for (Category category : parentCategoryList) {

            Button button = Util.createButton(getActivity(), category.getId(), params, category.getCategoryName());
            buttonList.add(button);
            rootLayout.addView(button);

            LinearLayout newLayout = Util.createLinearLayout(getActivity(), category.getId(), LinearLayout.VERTICAL, View.GONE);
            linearLayoutList.add(newLayout);

            CheckBox checkBox = Util.createCheckbox(getActivity(), category.getId(), params, "HepsiniSeç");//Hepsini seç checkbox'ı oluşturuluyor
            allSelectCheckBox.add(checkBox);
            newLayout.addView(checkBox);

            for (Category subCategory : childCategoryList) { //alt kategoriler için checkbox oluşturuluyor
                if (subCategory.getParentCategory().getId() == category.getId()) {
                    checkBox = Util.createCheckbox(getActivity(), subCategory.getId(), params, subCategory.getCategoryName());
                    checkboxList.add(checkBox);
                    newLayout.addView(checkBox);
                }
            }
            button = Util.createButton(getActivity(), category.getId(), params, "Firma Seç");
            selectCompanyButtonList.add(button);
            newLayout.addView(button);

            rootLayout.addView(newLayout);
        }
        Button button = Util.createButton(getActivity(), 0, params, "Tercihlerimi Kaydet");
        button.setEnabled(false);
        savePreferences=button;
        button.setOnClickListener(new View.OnClickListener() {

            final Callback createCallback = new Callback() {
                @Override
                public void success(Object o, Response response) {
                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    getActivity().startActivity(intent);
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                }
            };
            final Callback deleteCallback = new Callback() {
                @Override
                public void success(Object o, Response response) {
                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    getActivity().startActivity(intent);
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                }
            };

            @Override
            public void onClick(View v) {
                String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
                ProgressDialog.show(getActivity(),"","İşleminiz Yürütülüyor. Lütfen bekleyin.");
                List<CompanyCategory> newList = new ArrayList<>();
                List<CompanyCategory> newAddingList = new ArrayList<>();
                List<Preference> deletingList = new ArrayList<>();
                boolean addNewList = false;
                for (Preference preference : preferencesList) {
                    addNewList = true;
                    for (CompanyCategory companyCategory : selectedCompanyList) {
                        if (preference.getCategoryId().getId() == companyCategory.getCategoryId().getId() && preference.getCompanyId().getId() == companyCategory.getCompanyId().getId()) {
                            addNewList = false;
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
                        } else {
                            addNewList = true;
                        }
                    }
                    if (addNewList) {
                        newAddingList.add(companyCategory);
                    }
                }
                if (deletingList.size() != 0) {
                    RetrofitConfiguration.getRetrofitService(true).deleteUserPreferences(tokenKey, deletingList, deleteCallback);
                }
                if (newAddingList.size() != 0) {
                    RetrofitConfiguration.getRetrofitService(true).createUserPreferences(tokenKey, newAddingList, createCallback);
                }
            }
        });
        rootLayout.addView(button);

        for (Preference preference : preferencesList) {
            for (Category category : childCategoryList) {
                CheckBox chk = (CheckBox) getView().findViewById(category.getId());
                if (preference.getCategoryId().getId() == chk.getId()) {
                    chk.setChecked(true);
                }
            }

        }
        setEvents(savePreferences,checkboxList ,buttonList, allSelectCheckBox, linearLayoutList, selectCompanyButtonList);
    }

    public void setEvents(final Button savePreferences,List<CheckBox> checkBoxList,List<Button> buttonList, List<CheckBox> allSelectCheckBox, final List<LinearLayout> linearLayoutList, final List<Button> selectCompanyButtonList) {

        for (final CheckBox checkBox : checkBoxList) {//Kullanıcı takip ettiği bir kategoriyi kaldırırsa ona ve bu kategori selectedCompany listesinde bulunuyosa listeden kaldırılır.
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        for (CompanyCategory companyCategory : selectedCompanyList) {
                            if (checkBox.getId() == companyCategory.getCategoryId().getId()) {
                                selectedCompanyList.remove(companyCategory);
                                break;
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
                            if (checkBox.getId() == companyCategory.getCategoryId().getId()) {
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
                    boolean checked = false;
                    for (LinearLayout linearLayout : linearLayoutList) {
                        if (linearLayout.getId() == checkBox.getId()) {
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

                    for (LinearLayout linearLayout : linearLayoutList) {//seçili olan kategorileri bulmak için
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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Firmalarınızı Seçiniz");
                    builder.create().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                    final LinearLayout newLayout = new LinearLayout(getActivity());
                    newLayout.setOrientation(LinearLayout.VERTICAL);

                    for (final int categoryId : categoryIdList) {
                        for (final CompanyCategory companyCategory : companyList) {
                            if (categoryId == companyCategory.getCategoryId().getId()) {
                                showDialog=true;
//                                companyIdList.add(companyCategory.getCompanyId().getId());
                                final CheckBox checkBox = Util.createCheckbox(getActivity(), companyCategory.getId(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), companyCategory.getCompanyId().getCompanyName());
                                if (companyIdList.indexOf(companyCategory.getCompanyId().getId()) != -1) {
                                    checkBox.setVisibility(View.GONE);
                                }
                                if (Util.companyCategoryFindId(selectedCompanyList, companyCategory.getId()) != -1) {
                                    checkBox.setChecked(true);
//                                    int removeCheckboxIndex = Util.findIndexForCheckboxList(companyCategoryCheckedList, checkBox);
//                                    if (removeCheckboxIndex == -1) {
//                                        companyCategoryCheckedList.add(checkBox);
//                                        tempCompanyCategoryCheckedList.add(checkBox);
//                                    }
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
                                                            int removeCompanyCategoryIndex = Util.companyCategoryFindId(selectedCompanyList, checkBoxItem.getId());
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
                                    if (checkbox.getId() == companyCategory.getId() && selectedCompanyList.indexOf(companyCategory) == -1) {
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
                                    if (checkBox.getId() == companyCategory.getId() && selectedCompanyList.indexOf(companyCategory) == -1) {
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
                        Toast.makeText(getActivity(), "Gösterilecek Firma Yok", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }
}
