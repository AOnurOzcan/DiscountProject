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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.UserActivity;
import com.example.ooar.discountproject.model.Branch;
import com.example.ooar.discountproject.model.Notification;
import com.example.ooar.discountproject.model.Product;
import com.example.ooar.discountproject.model.User;
import com.example.ooar.discountproject.model.UserProduct;
import com.example.ooar.discountproject.util.FragmentChangeListener;
import com.example.ooar.discountproject.util.RetrofitConfiguration;
import com.example.ooar.discountproject.util.Util;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Onur Kuru on 22.3.2016.
 */
public class NotificationDetailFragment extends Fragment {

    Notification notification;
    TextView notificationName;
    TextView startDate;
    TextView endDate;
    TextView description;
    int notificationId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Session", getActivity().MODE_PRIVATE).edit();
        editor.putInt("NotificationId", 0).commit();
        notificationId = getArguments().getInt("notificationId");
        return inflater.inflate(R.layout.notification_detail, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        getNotification();
    }

    public void getNotification() {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                notification = (Notification) o;
                notification.setStartDate(Util.parseDate(notification.getStartDate()));
                notification.setEndDate(Util.parseDate(notification.getEndDate()));
                notification.setSendDate(Util.parseDate(notification.getSendDate()));
                renderPage();
                Util.stopProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Sunucudan yanıt alınamadı!", Toast.LENGTH_LONG).show();
                Util.stopProgressDialog();
            }
        };
        Util.startProgressDialog();
        String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
        RetrofitConfiguration.getRetrofitService().getNotificationById(tokenKey, notificationId, callback);
    }

    public void renderPage() {

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ScrollView scrollView = (ScrollView) getActivity().findViewById(R.id.scrollView);

        LinearLayout parent = (LinearLayout) inflater.inflate(R.layout.notification_detail_root_view, null);
        LinearLayout notificationDetailLayout = (LinearLayout) parent.findViewById(R.id.notificationDetailLayout);
        final LinearLayout notificationBranchLayout = (LinearLayout) parent.findViewById(R.id.notificationBranch);
        parent.removeView(notificationDetailLayout);

        int[] pixels = Util.getScreenPixels(getActivity());
        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(pixels[0] / 3, pixels[1] / 6);

        for (int i = 0; i < notification.getProductList().size(); i++) {
            Product product = notification.getProductList().get(i);
            View custom = inflater.inflate(R.layout.notification_product_view, null);

            TextView productName = (TextView) custom.findViewById(R.id.productName);
            productName.setText(product.getProductName());

            TextView price = (TextView) custom.findViewById(R.id.price);
            price.setText(String.valueOf(product.getPrice()));

            TextView previousPrice = (TextView) custom.findViewById(R.id.previousPrice);
            previousPrice.setText(String.valueOf(product.getPreviousPrice()));

            TextView stock = (TextView) custom.findViewById(R.id.stock);
            stock.setText(String.valueOf(product.getStock()));

            TextView description = (TextView) custom.findViewById(R.id.description);
            description.setText(product.getProductDescription());

            ImageView image = (ImageView) custom.findViewById(R.id.productImage);
            image.setLayoutParams(imageViewParams);

            final CheckBox checkBox = (CheckBox) custom.findViewById(R.id.addToList);
            if (product.getFollowList().size() != 0) {
                checkBox.setChecked(true);
                checkBox.setTag(product.getFollowList().get(0).getId());
            } else {
                checkBox.setChecked(false);
                checkBox.setTag(product.getId());
            }

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        final UserProduct userProduct = new UserProduct();
                        Product tempProduct = new Product();
                        User tempUser = new User();

                        tempProduct.setId((Integer) checkBox.getTag());
                        userProduct.setProductId(tempProduct);
                        userProduct.setUserId(tempUser);

                        Callback callback = new Callback() {
                            @Override
                            public void success(Object o, Response response) {
                                int userProductId = (int) o;
                                checkBox.setTag(userProductId);
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        };
                        String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
                        RetrofitConfiguration.getRetrofitService().createUserProduct(tokenKey, userProduct, callback);
                    } else {

                    }
                }
            });

            new Util.DownloadImageTask(image).execute(product.getImageURL());
            parent.addView(custom);
        }

        for (int i = 0; i < notification.getBranchList().size(); i++) {
            Branch branch = notification.getBranchList().get(i);
            View custom = inflater.inflate(R.layout.notification_branch_view, null);

            final TextView productName = (TextView) custom.findViewById(R.id.branch);
            productName.setText(branch.getName());

            productName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Branch tempBranch = null;
                    for (int i = 0; i < notification.getBranchList().size(); i++) {
                        tempBranch = notification.getBranchList().get(i);
                        if (tempBranch.getName().equals(productName.getText())) {
                            break;
                        }
                    }

                    if (tempBranch != null) {
                        BranchDetailFragment.branch = tempBranch;
                        FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                        fc.replaceFragment(new BranchDetailFragment(), "branchFragment");
                    }
                }
            });

            notificationBranchLayout.addView(custom);
        }

        parent.addView(notificationDetailLayout);
        scrollView.addView(parent);

        setOtherContent();
    }

    public void setOtherContent() {
        notificationName = (TextView) getActivity().findViewById(R.id.notificationName);
        startDate = (TextView) getActivity().findViewById(R.id.startDate);
        endDate = (TextView) getActivity().findViewById(R.id.endDate);
        description = (TextView) getActivity().findViewById(R.id.productDescription);

        notificationName.setText(notification.getName());
        startDate.setText(notification.getStartDate());
        endDate.setText(notification.getEndDate());
        description.setText(notification.getDescription());
    }

}