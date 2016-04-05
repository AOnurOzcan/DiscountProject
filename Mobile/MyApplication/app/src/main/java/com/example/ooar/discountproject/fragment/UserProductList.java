package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
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

        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.userProductListScroll);
        final LinearLayout parent = (LinearLayout) scrollView.findViewById(R.id.userProductsRootLayout);

        if(userProductList.size() == 0){

            View custom = inflater.inflate(R.layout.empty_content_layout, null);
            TextView textView = (TextView) custom.findViewById(R.id.emptyContentText);
            textView.setText("Alışveriş Listen Boş.\nBir ürün eklemek için herhangi bir bildirimin detayına git.");
            parent.removeAllViews();
            parent.addView(custom);

        }else{

            Button clearList = (Button) parent.findViewById(R.id.clearList);
            clearList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Callback callback = new Callback() {

                        @Override
                        public void success(Object o, Response response) {
                            userProductList.removeAll(userProductList);
                            Util.stopProgressDialog();
                            View custom = inflater.inflate(R.layout.empty_content_layout, null);
                            TextView textView = (TextView) custom.findViewById(R.id.emptyContentText);
                            textView.setText("Alışveriş Listen Boş.\nBir ürün eklemek için herhangi bir bildirimin detayına git.");
                            parent.removeAllViews();
                            parent.addView(custom);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            ErrorHandler.handleError(UserProductList.this.getActivity(), error);
                        }
                    };
                    if(userProductList.size() != 0){
                        String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
                        RetrofitConfiguration.getRetrofitService(true).deleteAllUserProduct(tokenKey, callback);
                    }
                }
            });

            int[] pixels = Util.getScreenPixels(getActivity());
            FrameLayout.LayoutParams imageViewParams = new FrameLayout.LayoutParams(pixels[0] / 3, pixels[1] / 6);

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

                final ImageView image = (ImageView) custom.findViewById(R.id.productImage);
                image.setLayoutParams(imageViewParams);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog settingsDialog = new Dialog(getActivity());
                        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.image_preview, null);
                        settingsDialog.setContentView(linearLayout);
                        final ImageView imageView = (ImageView) linearLayout.findViewById(R.id.imagePreview);
                        if (image.getDrawable() != null) {
                            imageView.setImageBitmap(((BitmapDrawable) image.getDrawable()).getBitmap());
                            Util.initializeZoomVariables();
                            imageView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    Util.imageZoom(v, event);
                                    return true;
                                }
                            });
                            settingsDialog.show();
                        }
                    }
                });

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
}
