package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.model.Category;
import com.example.ooar.discountproject.model.City;
import com.example.ooar.discountproject.model.User;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

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
    RadioGroup radioSexGroup;
    RadioButton radioSexButton;
    Spinner citySpinner;
    Button button;
    boolean callbackCategorySuccess = false;
    boolean callbackUserSuccess = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_profil, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        getAllCity(view);
        getAllCategories();
        setOnClickListener(view);
    }

    public void setOnClickListener(final View view) {
        button = (Button) view.findViewById(R.id.createProfil);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                City city = new City();

                firstNameText = (EditText) view.findViewById(R.id.firstName);
                lastNameText = (EditText) view.findViewById(R.id.lastName);
                radioSexGroup = (RadioGroup) view.findViewById(R.id.radioSexGroup);
                radioSexButton = (RadioButton) view.findViewById(radioSexGroup.getCheckedRadioButtonId());
                citySpinner = (Spinner) view.findViewById(R.id.citySpinner);
                city.setCityName(citySpinner.getSelectedItem().toString());

                Map<String, Object> validationMapper = new Hashtable<String, Object>();
                validationMapper.put("firstName", firstNameText.getText());
                validationMapper.put("lastName", lastNameText.getText());

                if (Util.checkValidation(validationMapper)) {

                    user.setFirstName(String.valueOf(firstNameText.getText()));
                    user.setLastName(String.valueOf(lastNameText.getText()));
                    user.setPhone(getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("phoneNumber", ""));
                    user.setNotificationOpen(true);
                    user.setBirthday(new Date());
                    user.setCityId(city);
//TODO date parse eklenecek spinner başlayacak
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

    public void createUserProfil(User user) {

        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {

                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).edit();
                editor.putString("tokenKey", String.valueOf(o)).commit();
                callbackUserSuccess = true;
                if(callbackCategorySuccess == true){
                    //Spinner kapat
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(new UserPreferencesFragment());
                }else {
                    //Spinner devam edecek
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Bir Hata Oluştu!", Toast.LENGTH_LONG).show();
            }
        };

        RetrofitConfiguration.getRetrofitService().createUser(user, callback);
    }

    public void getAllCity(final View view) {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                List<City> cityList = (List<City>) o;
                List<String> cityNameList = new ArrayList<String>();
                for (City city : cityList) {
                    cityNameList.add(city.getCityName());
                }
                citySpinner = (Spinner) view.findViewById(R.id.citySpinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, cityNameList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(adapter);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitConfiguration.getRetrofitService().getAllCity(callback);
    }

    public void getAllCategories(){
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                UserPreferencesFragment.categoryList = (List<Category>) o;
                callbackCategorySuccess = true;
                if(callbackUserSuccess == true){
                    //spinner kapat
                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(new UserPreferencesFragment());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        };

        RetrofitConfiguration.getRetrofitService().getAllCategories(callback);
    }
}
