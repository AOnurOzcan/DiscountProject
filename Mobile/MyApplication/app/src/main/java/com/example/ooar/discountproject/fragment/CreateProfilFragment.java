package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.model.Category;
import com.example.ooar.discountproject.model.City;
import com.example.ooar.discountproject.model.CompanyCategory;
import com.example.ooar.discountproject.model.User;
import com.example.ooar.discountproject.util.ErrorHandler;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Onur Kuru on 6.3.2016.
 */
public class CreateProfilFragment extends Fragment {

    EditText firstNameText;
    EditText lastNameText;
    Spinner birthDayText;
    RadioGroup radioSexGroup;
    RadioButton radioSexButton;
    Spinner citySpinner;
    Button button;
    private boolean callbackCategorySuccess = false;//diğer sayfa için callbackler tanımlanıyor
    private boolean callbackUserSuccess = false;
    private boolean callbackCompanySuccess = false;
    private boolean callbackCitySuccess = false;
    public static boolean datePickerIsShow = false;
    private List<City> cityList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_profil, container, false);//content basılıyor
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        getAllCity(view);//veriler alınıyor
        getAllCategories();
        getAllCompanyWithCategory();
        setOnClickListener(view);

        firstNameText = (EditText) view.findViewById(R.id.firstName);
        firstNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView textView = (TextView) view.findViewById(R.id.firstnameText);
                if (s.toString().length() > 0) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        });
        lastNameText = (EditText) view.findViewById(R.id.lastName);
        lastNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView textView = (TextView) view.findViewById(R.id.lastnameText);
                if (s.toString().length() > 0) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        });
        radioSexGroup = (RadioGroup) view.findViewById(R.id.radioSexGroup);
        radioSexButton = (RadioButton) view.findViewById(radioSexGroup.getCheckedRadioButtonId());
        citySpinner = (Spinner) view.findViewById(R.id.citySpinner);
        birthDayText = (Spinner) view.findViewById(R.id.birthDayEditText);
        List<String> adapterString = new ArrayList<>();
        adapterString.add("");
        ArrayAdapter adapter = new ArrayAdapter(getActivity().getApplicationContext(), R.layout.spinner_item, adapterString);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        birthDayText.setAdapter(adapter);
        birthDayText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!datePickerIsShow) {
                    showDatePickerDialog(v);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public void setOnClickListener(final View view) {
        button = (Button) view.findViewById(R.id.createProfil);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                City city = new City();

                for (City tempCity : cityList) {
                    if (tempCity.getCityName().equals(citySpinner.getSelectedItem().toString())) {
                        city = tempCity;
                        break;
                    }
                }

                String firstName = String.valueOf(firstNameText.getText());
                String lastName = String.valueOf(lastNameText.getText());

                Map<String, Object> validationMapper = new Hashtable<String, Object>();
                validationMapper.put("firstName", firstName);
                validationMapper.put("lastName", lastName);

                if (Util.checkValidation(validationMapper)) {

                    firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
                    lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setPhone(getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("phoneNumber", ""));
                    user.setRegistrationId(getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("regId", ""));
                    user.setNotificationOpen(true);
                    user.setBirthday(String.valueOf(birthDayText.getSelectedItem().toString()));
                    user.setCityId(city);

                    if (radioSexButton.getText().equals("Erkek")) {
                        user.setGender(true);
                    } else {
                        user.setGender(false);
                    }
                    createUserProfil(user);

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Hatalı Giriş", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //user oluşturma fonksiyonu
    public void createUserProfil(User user) {

        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {

                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).edit();
                editor.putString("tokenKey", String.valueOf(o)).commit();
                callbackUserSuccess = true;
                if (callbackCategorySuccess == true && callbackCompanySuccess == true) {
                    Util.stopProgressDialog();
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(new UserPreferencesFragment(), null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ErrorHandler.handleError(CreateProfilFragment.this.getActivity(), error);
            }
        };
        Util.startProgressDialog();
        RetrofitConfiguration.getRetrofitService().createUser(user, callback);
    }

    public void getAllCity(final View view) {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                callbackCitySuccess = true;
                cityList = (List<City>) o;
                List<String> cityNameList = new ArrayList<String>();
                for (City city : cityList) {
                    cityNameList.add(city.getCityName());
                }
                citySpinner = (Spinner) view.findViewById(R.id.citySpinner);
                ArrayAdapter adapter = new ArrayAdapter(getActivity().getApplicationContext(), R.layout.spinner_item, cityNameList);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                citySpinner.setAdapter(adapter);
                Util.stopProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                ErrorHandler.handleError(CreateProfilFragment.this.getActivity(), error);
            }
        };
        Util.startProgressDialog();
        RetrofitConfiguration.getRetrofitService().getAllCity(callback);
    }

    public void getAllCategories() {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                UserPreferencesFragment.categoryList = (List<Category>) o;
                callbackCategorySuccess = true;
                if (callbackUserSuccess == true && callbackCompanySuccess == true) {
                    if (callbackCitySuccess) {
                        Util.stopProgressDialog();
                    }
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(new UserPreferencesFragment(), null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ErrorHandler.handleError(CreateProfilFragment.this.getActivity(), error);
            }
        };

        RetrofitConfiguration.getRetrofitService().getAllCategories(callback);
    }

    public void getAllCompanyWithCategory() {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                UserPreferencesFragment.companyList = (List<CompanyCategory>) o;
                callbackCompanySuccess = true;
                if (callbackUserSuccess == true && callbackCategorySuccess == true) {
                    if (callbackCitySuccess) {
                        Util.stopProgressDialog();
                    }
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(new UserPreferencesFragment(), null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ErrorHandler.handleError(CreateProfilFragment.this.getActivity(), error);
            }
        };

        RetrofitConfiguration.getRetrofitService().getAllCompanyWithCategory(callback);
    }

    public void showDatePickerDialog(View v) {
        datePickerIsShow = true;
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getFragmentManager(), "datePicker");
    }
}
