package com.example.ooar.discountproject.fcm;

/**
 * Created by Onur Kuru on 21.3.2016.
 */

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.MainActivity;
import com.example.ooar.discountproject.activity.UserActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

public class FCMReceiver extends FirebaseMessagingService {

    private static final int MY_NOTIFICATION_ID = 1;
    private static int number = 0;
    NotificationManager notificationManager;
    Notification myNotification;
    Integer notificationId;
    String notificationTitle = "";
    String notificationContent = "";
    Intent myIntent;
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage != null) {

//            notificationId = Integer.parseInt(intent.getExtras().getString("notificationId"));
            notificationId = Integer.parseInt(remoteMessage.getData().get("id"));
            notificationTitle = remoteMessage.getData().get("title");
            notificationContent = remoteMessage.getData().get("content");
            Context context = getApplicationContext();

            number = context.getSharedPreferences("Session", Activity.MODE_PRIVATE).getInt("NotificationCount", 0);

            if (number == 0) {
                if (UserActivity.isOpen) {
                    myIntent = new Intent(context, UserActivity.class);
                    myIntent.putExtra("notificationId", notificationId);
                    myIntent.putExtra("fragmentName", "notificationDetailFragment");
                } else {
                    myIntent = new Intent(context, MainActivity.class);
                    myIntent.putExtra("notificationId", notificationId);
                }

                number++;
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
                SharedPreferences.Editor editor = context.getSharedPreferences("Session", context.MODE_PRIVATE).edit();
                editor.putInt("NotificationCount", number).commit();
                editor.putInt("notificationId", notificationId).commit();
                myNotification = new NotificationCompat.Builder(context)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationContent)
                        .setTicker("1 Yeni İndirim!")
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.notification)
                        .build();
            } else {
                if (UserActivity.isOpen) {
                    myIntent = new Intent(context, UserActivity.class);
                    myIntent.putExtra("fragmentName", "notificationsFragment");
                } else {
                    myIntent = new Intent(context, MainActivity.class);
                }

                number++;
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
                SharedPreferences.Editor editor = context.getSharedPreferences("Session", context.MODE_PRIVATE).edit();
                editor.putInt("NotificationCount", number).commit();
                myNotification = new NotificationCompat.Builder(context)
                        .setContentTitle("indirimİN")
                        .setContentText("Yeni İnrimlerin var!")
                        .setTicker(number + " yeni İndirim!")
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.notification)
                        .build();
                editor.putInt("NotificationId", 0).commit();
            }

            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
        }
    }
}