package com.example.ooar.discountproject.util;

import android.app.Activity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.ooar.discountproject.model.CompanyCategory;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Onur Kuru on 6.3.2016.
 */
public class Util {
    public static boolean checkValidation(Map<String, Object> object) {
        String pattern;
        Pattern regex;
        Matcher m;
        for (Map.Entry<String, Object> entry : object.entrySet()) {

            switch (entry.getKey()) {
                case "phoneNumber":
                    pattern = "^5[3-5]{1}[0-9]{8}$";
                    regex = Pattern.compile(pattern);
                    m = regex.matcher(entry.getValue().toString());
                    if (!m.find()) {
                        return false;
                    }
                    break;
                case "confirmationCode":
                    pattern = "^[0-9]{4}$";
                    regex = Pattern.compile(pattern);
                    m = regex.matcher(entry.getValue().toString());
                    if (!m.find()) {
                        return false;
                    }
                    break;
                case "firstName":
                case "lastName":
                    pattern = "^[\\p{L}]{3,20}+$";
                    regex = Pattern.compile(pattern);
                    m = regex.matcher(entry.getValue().toString());
                    if (!m.find()) {
                        return false;
                    }
                    break;
                default:
                    return false;
            }
        }

        return true;
    }

    public static int findIndexForCheckboxList(List<CheckBox> checkBoxList, CheckBox checkBox) {
        int index = -1;
        for (int i = 0; i < checkBoxList.size(); i++) {
            if (checkBoxList.get(i).getId() == checkBox.getId()) {
                index = i;
                break;
            } else {
                index = -1;
            }
        }
        return index;
    }

    public static int companyCategoryFindId(List<CompanyCategory> companyCategoryList, int id) {

        int index = -1;
        for (int i = 0; i < companyCategoryList.size(); i++) {
            if (companyCategoryList.get(i).getId() == id) {
                index = i;
                break;
            } else {
                index = -1;
            }
        }
        return index;
    }

    public static Button createButton(Activity activity, int id, LinearLayout.LayoutParams layoutParams, String text) {
        Button button = new Button(activity);
        if (id != 0)
            button.setId(id);
        if (layoutParams != null)
            button.setLayoutParams(layoutParams);
        if (text != null)
            button.setText(text);
        return button;
    }

    public static CheckBox createCheckbox(Activity activity, int id, LinearLayout.LayoutParams layoutParams, String text) {
        CheckBox checkBox = new CheckBox(activity);
        if (id != 0)
            checkBox.setId(id);
        if (layoutParams != null)
            checkBox.setLayoutParams(layoutParams);
        if (text != null)
            checkBox.setText(text);
        return checkBox;
    }

    public static LinearLayout createLinearLayout(Activity activity, int id, int oriented, int visibility) {
        LinearLayout linearLayout = new LinearLayout(activity);
        if (id != 0)
            linearLayout.setId(id);
        linearLayout.setOrientation(oriented);
        linearLayout.setVisibility(visibility);
        return linearLayout;
    }
}
