package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.model.Product;
import com.example.ooar.discountproject.model.User;
import com.example.ooar.discountproject.model.UserProduct;
import com.example.ooar.discountproject.util.ErrorHandler;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Onur Kuru on 30.3.2016.
 */
public class UserProductList extends Fragment {

    List<UserProduct> userProductList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_product_list_root, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        getUserProducts();
    }

    public void getUserProducts() {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                userProductList = (List<UserProduct>) o;
                Util.stopProgressDialog();
                renderPage();
            }

            @Override
            public void failure(RetrofitError error) {
                ErrorHandler.handleError(UserProductList.this.getActivity(), error);
            }
        };
        String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
        RetrofitConfiguration.getRetrofitService(true).getAllUserProducts(tokenKey, callback);
    }

    public void renderPage() {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.userProductListScroll);
        LinearLayout parent = (LinearLayout) scrollView.findViewById(R.id.userProductsRootLayout);

        int[] pixels = Util.getScreenPixels(getActivity());
        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(pixels[0] / 3, pixels[1] / 6);

        for (final UserProduct userProduct : userProductList) {
            final View custom = inflater.inflate(R.layout.notification_product_view, null);
            TextView productName = (TextView) custom.findViewById(R.id.productName);
            productName.setText(userProduct.getProductId().getProductName());

            TextView price = (TextView) custom.findViewById(R.id.price);
            price.setText(String.valueOf(userProduct.getProductId().getPrice()));

            TextView previousPrice = (TextView) custom.findViewById(R.id.previousPrice);
            previousPrice.setText(String.valueOf(userProduct.getProductId().getPreviousPrice()));

            TextView stock = (TextView) custom.findViewById(R.id.stock);
            stock.setText(String.valueOf(userProduct.getProductId().getStock()));

            TextView description = (TextView) custom.findViewById(R.id.description);
            description.setText(userProduct.getProductId().getProductDescription());

            ImageView image = (ImageView) custom.findViewById(R.id.productImage);
            image.setLayoutParams(imageViewParams);

            final CheckBox checkBox = (CheckBox) custom.findViewById(R.id.addToList);
            checkBox.setChecked(true);
            checkBox.setText("Alışveriş Listemden Kaldır");
            checkBox.setTag(userProduct.getId());

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        Callback callback = new Callback() {
                            @Override
                            public void success(Object o, Response response) {
                                userProductList.remove(userProduct);
                                Util.stopProgressDialog();
                                Toast.makeText(getActivity().getApplicationContext(), "Başarıyla Kaydedildi", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                ErrorHandler.handleError(UserProductList.this.getActivity(), error);
                            }
                        };
                        String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
                        RetrofitConfiguration.getRetrofitService(true).deleteUserProduct(tokenKey, (Integer) checkBox.getTag(), callback);

                        ((ViewGroup) custom.getParent()).removeView(custom);
                    }
                }
            });

            new Util.DownloadImageTask(image).execute(userProduct.getProductId().getImageURL());
            parent.addView(custom);
        }
    }
}
