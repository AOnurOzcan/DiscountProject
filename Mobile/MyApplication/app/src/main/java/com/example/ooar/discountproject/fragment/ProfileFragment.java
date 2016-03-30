package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.UserActivity;
import com.example.ooar.discountproject.model.Category;
import com.example.ooar.discountproject.model.City;
import com.example.ooar.discountproject.model.User;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by Ramos on 06.03.2016.
 */
public class ProfileFragment extends Fragment {
    private static List<City> cityList = null;
    Spinner birthDay;
    Spinner city;
    EditText name;
    EditText lastName;
    EditText phone;
    RadioButton Man;
    RadioButton Woman;
    Button editButton;
    List<String> birthAdapterString = new ArrayList<>();
    List<String> cityNameList = new ArrayList<String>();
    public static boolean datePickerIsShow = false;
    public static User thisUser;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        if (cityList == null || thisUser == null) {
            getAllCity(view);
        } else {
            cityNameList.clear();
            setSpinner(view);
            setUserValue(thisUser, view);
        }
    }

    public void getUser(final View v) {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                ProfileFragment.thisUser = (User) o;
                setUserValue(thisUser, v);
                Util.stopProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };
        String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
        RetrofitConfiguration.getRetrofitService(true).getUser(tokenKey, callback);
    }

    public void getAllCity(final View view) {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                ProfileFragment.cityList = (List<City>) o;
                getUser(view);
                setSpinner(view);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };
        RetrofitConfiguration.getRetrofitService(true).getAllCity(callback);
    }

    public void setSpinner(View view) {
        for (City city : cityList) {
            cityNameList.add(city.getCityName());
        }
        city = (Spinner) view.findViewById(R.id.profilCitySpinner);
        ArrayAdapter adapter = new ArrayAdapter(getActivity().getApplicationContext(), R.layout.spinner_item, cityNameList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        city.setAdapter(adapter);
    }

    public void showDatePickerDialog(View v) {
        datePickerIsShow = true;
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getFragmentManager(), "datePicker");
    }

    public void setUserValue(User user, View view) {
        name = (EditText) view.findViewById(R.id.profilNameEditText);
        lastName = (EditText) view.findViewById(R.id.profilLastNameEditText);
        birthDay = (Spinner) view.findViewById(R.id.birthDayEditText);
        phone = (EditText) view.findViewById(R.id.profilPhoneEditText);
        Woman = (RadioButton) view.findViewById(R.id.profilGenderWoman);
        Man = (RadioButton) view.findViewById(R.id.profilGenderMan);
        city = (Spinner) view.findViewById(R.id.profilCitySpinner);


        String[] dateText = user.getBirthday().split("");
        String year = dateText[1] + dateText[2] + dateText[3] + dateText[4];
        String month = dateText[6] + dateText[7];
        String day = dateText[9] + dateText[10];
        name.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        birthAdapterString.add(day + "/" + month + "/" + year);
        ArrayAdapter birthAdapter = new ArrayAdapter(getActivity().getApplicationContext(), R.layout.spinner_item, birthAdapterString);
        birthAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        birthDay.setAdapter(birthAdapter);
        birthDay.setOnTouchListener(new View.OnTouchListener() {
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

        phone.setText(user.getPhone());
        if (user.isGender() == true) {
            Man.setChecked(true);
        } else {
            Woman.setChecked(true);
        }
        int index = cityNameList.indexOf(user.getCityId().getCityName());
        city.setSelection(index);
        editButton = (Button) view.findViewById(R.id.profilSaveProfil);
        editButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              String firstNameVal = String.valueOf(name.getText());
                                              String lastNameVal = String.valueOf(lastName.getText());
                                              User user = new User();
                                              City usercity = new City();
                                              usercity.setCityName(city.getSelectedItem().toString());
                                              for (City citiess : cityList) {
                                                  if (city.getSelectedItem().toString() == citiess.getCityName()) {
                                                      usercity.setId(citiess.getId());
                                                  }
                                              }
                                              user.setFirstName(firstNameVal);
                                              user.setLastName(lastNameVal);
                                              user.setBirthday(String.valueOf(birthDay.getSelectedItem().toString()));
                                              if (Man.isChecked()) {
                                                  user.setGender(true);
                                              } else {
                                                  user.setGender(false);
                                              }
                                              user.setCityId(usercity);
                                              editUserProfil(user);
                                          }
                                      }

        );
    }

    public void editUserProfil(User user) {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                thisUser = null;
                FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                fc.replaceFragment(UserActivity.userTabsFragment, "userTabs");
                Util.stopProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), "Başarılı!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Util.stopProgressDialog();
                Toast.makeText(getActivity().getApplicationContext(), "Bir Hata Oluştu!", Toast.LENGTH_LONG).show();
            }
        };
        String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
//        ProgressDialog.show(getActivity(), "", "İşleminiz Yürütülüyor. Lütfen bekleyin.");
        RetrofitConfiguration.getRetrofitService(true).editUser(tokenKey, user, callback);
    }

    public void onBackPressed() {
        getActivity().finish();
    }
}
