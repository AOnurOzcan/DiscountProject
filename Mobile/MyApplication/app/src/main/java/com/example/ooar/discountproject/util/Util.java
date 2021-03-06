package com.example.ooar.discountproject.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.UserActivity;
import com.example.ooar.discountproject.model.Category;
import com.example.ooar.discountproject.model.Company;
import com.example.ooar.discountproject.model.CompanyCategory;
import com.example.ooar.discountproject.model.Preference;
import com.example.ooar.discountproject.model.User;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Onur Kuru on 6.3.2016.
 */

//Yardımcı fonksiyonların tanımlandığı sınıf
public class Util {
    public static ProgressDialog progressDialog;

    static Matrix matrix = new Matrix();
    static Matrix savedMatrix = new Matrix();
    static int NONE = 0;
    static int DRAG = 1;
    static int ZOOM = 2;
    static int mode = NONE;
    static PointF start = new PointF();
    static PointF mid = new PointF();
    static float oldDist = 1f;

    public static boolean checkValidation(Map<String, Object> object) {//validasyon fonksiyonu
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

    public static int findIndexForCheckboxList(List<CheckBox> checkBoxList, CheckBox checkBox) {//verilen listeden tag'i aynı olan index döner
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

    public static CheckBox findCheckboxById(List<CheckBox> checkBoxList, int id) {//verilen listeden tagi id ye eşit olan checkbox getirilir
        CheckBox checkBox = null;
        for (int i = 0; i < checkBoxList.size(); i++) {
            if (checkBoxList.get(i).getTag().equals(id)) {
                checkBox = checkBoxList.get(i);
                break;
            }
        }
        return checkBox;
    }

    public static int companyCategoryFindId(List<CompanyCategory> companyCategoryList, int id) {//verilen listeden id si verilen id ye eşit olan index döner

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

    public static CheckBox createCheckbox(Activity activity, int id, LinearLayout.LayoutParams layoutParams, String text) {//checkbox oluşturma
        CheckBox checkBox = new CheckBox(activity);
        if (id != 0)
            checkBox.setTag(id);
        if (layoutParams != null)
            checkBox.setLayoutParams(layoutParams);
        if (text != null)
            checkBox.setText(text);
        return checkBox;
    }

    public static void startProgressDialog() {//progressdialog başlatma
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public static void stopProgressDialog() {//progressdialog durdurma
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void setProgressDialog(Activity activity) {
        progressDialog = new ProgressDialog(activity, R.style.StyledDialog);
        progressDialog.setCancelable(false);
    }

    public static String parseDate(String date) {//tarih dönüştürme
        if (date != null) {
            String[] tempArray = date.split("T");
            String day = tempArray[0].split("-")[2];
            String mount = tempArray[0].split("-")[1];
            String year = tempArray[0].split("-")[0];

            return day + "/" + mount + "/" + year;
        } else {
            return null;
        }
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {//resim yükleme
        ImageView bmImage;
        ProgressBar imageProgressBar;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
            View view = (View) bmImage.getParent();
            this.imageProgressBar = (ProgressBar) view.findViewById(R.id.imageProgressBar);
            imageProgressBar.setVisibility(View.VISIBLE);
        }

        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap image = null;
            try {
                image = UserActivity.imageCache.getBitmapFromMemCache(imageUrl);
                if (image == null) {
                    InputStream in = new java.net.URL(imageUrl).openStream();
                    image = BitmapFactory.decodeStream(in);
                }
                UserActivity.imageCache.addBitmapToMemoryCache(imageUrl, image);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            imageProgressBar.setVisibility(View.GONE);
        }
    }

    public static int[] getScreenPixels(Activity activity) {//ekran genişliklerini getiren fonksiyon
        int[] pixels = new int[2];
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        pixels[0] = displaymetrics.widthPixels;
        pixels[1] = displaymetrics.heightPixels;
        return pixels;
    }

    public static boolean isEqual(List<Preference> preferenceList, List<CompanyCategory> selectedList) {// verilen iki listenin eşit olup olmadığını döndüren liste
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

    public static List<Category> getSubCategory(Category parentCategory, List<Category> subCategoryList) {//verilen listeden alt kategorileri döndürür
        List<Category> subCategories = new ArrayList<>();
        for (Category category : subCategoryList) {
            if (category.getParentCategory() == parentCategory.getId()) {
                subCategories.add(category);
            }
        }
        return subCategories;
    }

    public static Category findCategoryById(List<Category> categoryList, int id) {// verilen listden üst kategori döndürür
        Category category = null;
        for (Category tempCategory : categoryList) {
            if (tempCategory.getId() == id) {
                category = tempCategory;
                break;
            }
        }
        return category;
    }


    private static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public static void initializeZoomVariables(){
        matrix = new Matrix();
        savedMatrix = new Matrix();
        NONE = 0;
        DRAG = 1;
        ZOOM = 2;
        mode = NONE;
        start = new PointF();
        mid = new PointF();
        oldDist = 1f;
    }

    public static void imageZoom(View v, MotionEvent event){
        ImageView imageView = (ImageView) v;

        float scale;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) { //movement of first finger
                    matrix.set(savedMatrix);
                    if (imageView.getLeft() >= -392) {
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                    }
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        imageView.setImageMatrix(matrix);
    }


    private static float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private static void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

}
