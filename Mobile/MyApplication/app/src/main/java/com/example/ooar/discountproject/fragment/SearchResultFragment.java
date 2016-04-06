package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.model.Company;
import com.example.ooar.discountproject.model.Product;
import com.example.ooar.discountproject.model.User;
import com.example.ooar.discountproject.model.UserProduct;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;
import com.google.gson.Gson;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Ramos on 01.04.2016.
 */
public class SearchResultFragment extends Fragment {
    private List<Product> productList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        productList = (List<Product>) getArguments().getSerializable("list");
        return inflater.inflate(R.layout.search_result, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        getResult();
    }

    private void getResult() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.searchResultScrollView);

        LinearLayout parent = (LinearLayout) inflater.inflate(R.layout.search_result_image_view, null);

        int[] pixels = Util.getScreenPixels(getActivity());
        FrameLayout.LayoutParams imageViewParams = new FrameLayout.LayoutParams(pixels[0] / 3, pixels[1] / 6);


        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);

            View custom = inflater.inflate(R.layout.search_result_image_view, null);

            TextView productName = (TextView) custom.findViewById(R.id.searchResultProductName);
            productName.setText(product.getProductName());

            TextView productCompany = (TextView) custom.findViewById(R.id.searchResultCompany);
            productCompany.setText(product.getCompanyId().getCompanyName());

            ImageView image = (ImageView) custom.findViewById(R.id.searchResultProductImage);
            image.setLayoutParams(imageViewParams);

            new Util.DownloadImageTask(image).execute(product.getImageURL());
            //parent.addView(custom);
            scrollView.addView(custom);
        }

    }
}
