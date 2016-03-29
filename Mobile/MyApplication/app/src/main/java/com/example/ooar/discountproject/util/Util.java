package com.example.ooar.discountproject.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.model.Company;
import com.example.ooar.discountproject.model.CompanyCategory;
import com.example.ooar.discountproject.model.Preference;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Onur Kuru on 6.3.2016.
 */
public class Util {

    public static ProgressDialog progressDialog;

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
            if (checkBoxList.get(i).getTag().equals(checkBox.getTag())) {
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

    public static CheckBox createCheckbox(Activity activity, int id, LinearLayout.LayoutParams layoutParams, String text) {
        CheckBox checkBox = new CheckBox(activity);
        if (id != 0)
            checkBox.setTag(id);
        if (layoutParams != null)
            checkBox.setLayoutParams(layoutParams);
        if (text != null)
            checkBox.setText(text);
        return checkBox;
    }

    public static void startProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public static void stopProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void setProgressDialog(Activity activity) {
        progressDialog = new ProgressDialog(activity, R.style.StyledDialog);
        progressDialog.setCancelable(false);
    }

    public static String parseDate(String date) {
        String[] tempArray = date.split("T");
        String day = tempArray[0].split("-")[2];
        String mount = tempArray[0].split("-")[1];
        String year = tempArray[0].split("-")[0];

        return day + "/" + mount + "/" + year;
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public static int[] getScreenPixels(Activity activity) {
        int[] pixels = new int[2];
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        pixels[0] = displaymetrics.widthPixels;
        pixels[1] = displaymetrics.heightPixels;
        return pixels;
    }

    public static boolean isEqual(List<Preference> preferenceList, List<CompanyCategory> selectedList) {
        if (preferenceList.size() != selectedList.size()) {
            return false;
        } else {
            boolean[] isEqual = new boolean[preferenceList.size()];
            int i = 0;
            for (Preference preference : preferenceList) {
                for (CompanyCategory companyCategory : selectedList) {
                    if (companyCategory.getCompanyId().getId() == preference.getCompanyId().getId() && companyCategory.getCategoryId().getId() == preference.getCategoryId().getId()) {
                        isEqual[i] = true;
                        break;
                    } else {
                        isEqual[i] = false;
                    }
                }
                i++;
            }

            for (boolean equal : isEqual) {
                if (!equal) {
                    return false;
                }
            }
            return true;
        }
    }
}
