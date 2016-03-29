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
            button.setTag(category.getId());
            button.setText(category.getCategoryName());
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

                    checkboxList.add(subCheckbox);
                    checkboxLayout.addView(subCategoryLayout); //checkboxların toplandığı layouta gömülüyor
                }
            }

            if (!subCategorySizeBigZero) {
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

        setEvents(savePreferences, checkboxList, buttonList, linearLayoutList, editCompanyList);
    }

    public void setEvents(final Button savePreferences, List<CheckBox> checkBoxList, List<Button> buttonList, final List<LinearLayout> linearLayoutList, final List<Button> editCompanyButtonList) {

        final List<CheckBox> companyCategoryCheckedList = new ArrayList<>();
        final List<CheckBox> tempCompanyCategoryCheckedList = new ArrayList<>();

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
                        Button tempButton = (Button) ((ViewGroup) checkBox.getParent()).findViewById(R.id.editCompanyButton);
                        tempButton.setVisibility(View.GONE);
                    } else {
                        createBuilder(checkBox, null, companyCategoryCheckedList, tempCompanyCategoryCheckedList, savePreferences);
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

        final LinearLayout newLayout = new LinearLayout(getActivity());
        newLayout.setOrientation(LinearLayout.VERTICAL);

        for (CompanyCategory companyCategory : companyList) {
            if (categoryId == companyCategory.getCategoryId().getId()) {
                showDialog = true;
                final CheckBox companyCheckBox = Util.createCheckbox(getActivity(), companyCategory.getId(), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), companyCategory.getCompanyId().getCompanyName());
                if (Util.companyCategoryFindId(selectedCompanyList, companyCategory.getId()) != -1) {
                    companyCheckBox.setChecked(true);
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
                for (CheckBox checkbox : companyCategoryCheckedList) {
                    tempCompanyCategoryCheckedList.add(checkbox);
                    for (CompanyCategory companyCategory : companyList) {
                        if (checkbox.getTag().equals(companyCategory.getId()) && selectedCompanyList.indexOf(companyCategory) == -1) {
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
            if (button == null) {
                checkBox.setChecked(false);
            }
            Toast.makeText(getActivity(), "Gösterilecek Firma Yok", Toast.LENGTH_LONG).show();
        }

    }
}
