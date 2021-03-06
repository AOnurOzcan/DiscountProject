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
import com.example.ooar.discountproject.util.ErrorHandler;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Onur Kuru on 7.3.2016.
 */
public class UserPreferencesFragment extends Fragment {

    public static List<Category> categoryList;//kategorilerin toplandığı liste
    public static List<CompanyCategory> companyList;//firmaların toplandığı liste
    public List<CompanyCategory> selectedCompanyList = new ArrayList<>();//seçilen firma ve kategorilerin toplandığı liste

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.preference_layout, container, false);//content basılıyor
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        renderLayout();
    }

    public void renderLayout() {

        List<Category> parentCategoryList = new ArrayList<Category>();//ana kategorilerin toplandığı liste
        List<Category> childCategoryList = new ArrayList<Category>();//alt kategorilerin toplandıgı liste
        List<CheckBox> checkboxList = new ArrayList<CheckBox>();//tüm checkbox(alt ketegori checkboxlarının) ların toplandığı liste(hepsini seç hariç)
        List<CheckBox> selectAllCheckboxList = new ArrayList<CheckBox>();//hepsini seç checkboxlarınıntoplandığı liste
        List<LinearLayout> linearLayoutList = new ArrayList<LinearLayout>();//ana layoutların toplandığı liste
        List<LinearLayout> checkboxLayoutList = new ArrayList<LinearLayout>();// checkbox layoutlarının toplandığı liste
        List<Button> buttonList = new ArrayList<Button>();//üst kategorilere ait butonların toplandığı liste
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
            checkboxLayoutList.add(checkboxLayout);

            LinearLayout mainLayout = (LinearLayout) custom.findViewById(R.id.mainLayout);//root layout dan main layout alınıyor
            mainLayout.setTag(category.getId());
            linearLayoutList.add(mainLayout);


            Button button = (Button) custom.findViewById(R.id.parentCategory);//Parent kategori butonları alınıp yenı degerler set ediliyor
            button.setTag(category.getId());
            button.setText(category.getCategoryName());
            buttonList.add(button);

            CheckBox checkBox = (CheckBox) custom.findViewById(R.id.selectAll);
            checkBox.setTag(category.getId());
            selectAllCheckboxList.add(checkBox);

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
                checkBox.setVisibility(View.GONE);
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
                    ErrorHandler.handleError(UserPreferencesFragment.this.getActivity(), error);
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

        setEvents(savePreferences, checkboxList, buttonList, linearLayoutList, editCompanyList, selectAllCheckboxList, checkboxLayoutList, parentCategoryList, childCategoryList);
    }

    //Checkbox ve buton eventlarının verildiği fonksiyon
    public void setEvents(final Button savePreferences, final List<CheckBox> checkBoxList, List<Button> buttonList, final List<LinearLayout> linearLayoutList, final List<Button> editCompanyButtonList, final List<CheckBox> selectAllCheckboxList, final List<LinearLayout> checkboxLayoutList, final List<Category> parentCategoryList, final List<Category> childCategoryList) {

        final List<CheckBox> companyCategoryCheckedList = new ArrayList<>();//seçilen alt kategorilerin toplandığı liste
        final List<CheckBox> tempCompanyCategoryCheckedList = new ArrayList<>();//vazgeç butonu için seçili checkboxları hafızada tutan liste
        final boolean[] selectAll = {false};// hepsini seç checkboxları için diğer checkbox evenlarını tetiklememesi için bu değişken kullanılıyo

        //parentcategory listesinden ilgili categori bulunur.
        //childcategory listesinden ilgili alt kategroiler bulunur
        //daha sonra bulunan her alt kategoriye ait firmalar selected listesinde toplanır.
        for (final CheckBox checkBox : selectAllCheckboxList) { //hepsini seç checkboxlarının evenları atanıyor
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    selectAll[0] = true;//diğer eventların tetiklenmemesi için değişken true yapılıyo
                    Category parentCategory = Util.findCategoryById(parentCategoryList, (Integer) checkBox.getTag());
                    if (parentCategory != null) {
                        List<Category> subCategoryList = Util.getSubCategory(parentCategory, childCategoryList);
                        if (subCategoryList.size() != 0) {
                            for (Category subCategory : subCategoryList) {
                                CheckBox subCheckbox = Util.findCheckboxById(checkBoxList, subCategory.getId());
                                if (subCheckbox != null) {
                                    subCheckbox.setChecked(isChecked);
                                    if (isChecked) {
                                        Button tempButton = (Button) ((ViewGroup) subCheckbox.getParent()).findViewById(R.id.editCompanyButton);
                                        tempButton.setVisibility(View.VISIBLE);
                                        for (CompanyCategory companyCategory : companyList) {
                                            if (companyCategory.getCategoryId().getId() == subCategory.getId() && Util.companyCategoryFindId(selectedCompanyList, companyCategory.getId()) == -1) {
                                                selectedCompanyList.add(companyCategory);
                                            }
                                        }
                                        if (selectedCompanyList.size() > 0) {
                                            savePreferences.setEnabled(true);
                                        } else {
                                            savePreferences.setEnabled(false);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    selectAll[0] = false;//değişken false yapıalrak evenların daha sonra çalışması sağlanıyor
                }
            });
        }


        //ilgili alt kategoricheckbox ı tikli ise create builder fonksiyonu çalıştırılarak dialogun ekrana basılması sağlanır.
        //eğer tikli değilse selected listesinden ilgili firmalar kaldırılır.
        for (final CheckBox checkBox : checkBoxList) {//Kullanıcı takip ettiği bir kategoriyi kaldırırsa ona ve bu kategori selectedCompany listesinde bulunuyosa listeden kaldırılır.
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {

                        Iterator<CompanyCategory> iterator = selectedCompanyList.iterator();
                        while (iterator.hasNext()) {
                            CompanyCategory companyCategory = iterator.next();
                            CheckBox checkedCheckbox = Util.findCheckboxById(companyCategoryCheckedList, companyCategory.getId());
                            companyCategoryCheckedList.remove(checkedCheckbox);
                            CheckBox tempCheckbox = Util.findCheckboxById(tempCompanyCategoryCheckedList, companyCategory.getId());
                            tempCompanyCategoryCheckedList.remove(tempCheckbox);
                            if (checkBox.getTag().equals(companyCategory.getCategoryId().getId())) {
                                iterator.remove();
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
                        if (!selectAll[0]) {
                            createBuilder(checkBox, null, companyCategoryCheckedList, tempCompanyCategoryCheckedList, savePreferences);
                        }
                    }
                }
            });
        }

        //kullanının ilgili kategori için firmaları yeniden seçmesini sağlayan evenlar atanıyor
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


    //Firma seçmek için dialog ekrana basan fonksiyon
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

        CheckBox selectAllCheckbox = Util.createCheckbox(getActivity(), 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Hepsini Seç");
        selectAllCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int count = newLayout.getChildCount();
                for (int i = 0; i < count; i++) {
                    try {
                        CheckBox companyCheckbox = (CheckBox) newLayout.getChildAt(i);
                        if (!companyCheckbox.getText().equals("Hepsini Seç")) {
                            companyCheckbox.setChecked(isChecked);
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        });
        newLayout.addView(selectAllCheckbox);

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
