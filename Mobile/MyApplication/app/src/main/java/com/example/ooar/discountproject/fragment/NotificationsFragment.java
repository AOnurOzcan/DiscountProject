package com.example.ooar.discountproject.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.model.Notification;
import com.example.ooar.discountproject.model.UserNotification;
import com.example.ooar.discountproject.util.ErrorHandler;
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
public class NotificationsFragment extends Fragment {

    public static List<UserNotification> userNotificationList = null;
    private int notificationStartIndex = 0;
    private int notificationLength = 2;
    private boolean loadMore = false;
    private boolean loadAllContent = false;
    private LinearLayout loadMoreLayout;
    private Button loadMoreButton;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.notifications, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserNotifications(notificationStartIndex, userNotificationList.size());
                swipeContainer.setRefreshing(false);
            }
        });
        if (userNotificationList == null) {
            getUserNotifications(notificationStartIndex, notificationLength);
        } else {
            renderPage();
        }
    }

    public void getUserNotifications(int startIndex, int length) {
        Callback callback = new Callback() {
            @Override
            public void success(Object o, Response response) {
                if (loadMore) {
                    loadMore = false;
                    List<UserNotification> tempList = (List<UserNotification>) o;
                    if (tempList.size() == 0) {
                        loadAllContent = true;
                    } else {
                        loadAllContent = false;
                    }
                    userNotificationList.addAll(tempList);
                } else {
                    userNotificationList = (List<UserNotification>) o;
                    if (userNotificationList.size() == 0) {
                        loadAllContent = true;
                    } else {
                        loadAllContent = false;
                    }
                }
                renderPage();
                Util.stopProgressDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                ErrorHandler.handleError(NotificationsFragment.this.getActivity(), error);
            }
        };

        String tokenKey = getActivity().getSharedPreferences("Session", Activity.MODE_PRIVATE).getString("tokenKey", "");
        RetrofitConfiguration.getRetrofitService(true).getAllNotification(tokenKey, startIndex, length, callback);
    }

    public void renderPage() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.notificationRootLayout);

        loadMoreLayout = (LinearLayout) parent.findViewById(R.id.loadMoreLayout);
        loadMoreButton = (Button) parent.findViewById(R.id.loadMore);
        parent.removeAllViews();

        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore = true;
                getUserNotifications(userNotificationList.size(), notificationLength);
            }
        });

        for (int i = 0; i < userNotificationList.size(); i++) {
            final Notification notification = userNotificationList.get(i).getNotificationId();
            View custom = inflater.inflate(R.layout.notification_view, null);
            final Button notificationButton = (Button) custom.findViewById(R.id.notificationButton);
            notificationButton.setText(notification.getName());

            notificationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int notificationId = 0;
                    for (UserNotification userNotification : userNotificationList) {
                        if (userNotification.getNotificationId().getName().equals(notificationButton.getText())) {
                            notificationId = userNotification.getNotificationId().getId();
                            break;
                        }
                    }

                    Bundle bundle = new Bundle();
                    bundle.putInt("notificationId", notificationId);
                    Fragment notificationDetailFragment = new NotificationDetailFragment();
                    notificationDetailFragment.setArguments(bundle);

                    FragmentChangeListener fc = (FragmentChangeListener) getActivity();
                    fc.replaceFragment(notificationDetailFragment, "notificationDetail");
                }
            });
            parent.addView(custom);

        }

        parent.addView(loadMoreLayout);
        if (userNotificationList.size() >= notificationLength) {
            loadMoreLayout.setVisibility(View.VISIBLE);
        }
        if (loadAllContent) {
            loadMoreButton.setText("Tüm Bildirimlerin Bu Kadar");
            loadMoreButton.setClickable(false);
        } else {
            loadMoreButton.setText("Daha fazla yükle");
            loadMoreButton.setClickable(true);
        }
    }

}
