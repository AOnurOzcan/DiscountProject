package com.example.ooar.discountproject.gcm;

/**
 * Created by Onur Kuru on 21.3.2016.
 */

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.content.SharedPreferences;

import com.example.ooar.discountproject.R;
import com.example.ooar.discountproject.activity.MainActivity;
import com.example.ooar.discountproject.activity.UserActivity;
import com.example.ooar.discountproject.fragment.NotificationDetailFragment;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    private static final int MY_NOTIFICATION_ID = 1;
    private static int number = 0;
    NotificationManager notificationManager;
    Notification myNotification;
    int notificationId=0;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationId = Integer.parseInt(intent.getExtras().getString("key1"));
        number = context.getSharedPreferences("Session", Activity.MODE_PRIVATE).getInt("NotificationCount", 0);
        if (number == 0) {
            Intent myIntent = new Intent(context, MainActivity.class);
            myIntent.putExtra("notificationId", notificationId);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    myIntent,
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            number++;
            SharedPreferences.Editor editor = context.getSharedPreferences("Session", context.MODE_PRIVATE).edit();
            editor.putInt("NotificationCount", number).commit();
            myNotification = new NotificationCompat.Builder(context)
                    .setContentTitle("One!")
                    .setContentText("Do Something...")
                    .setTicker("Notification!")
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.notification)
                    .build();
            // editor.putInt("NotificationId", Integer.parseInt(intent.getExtras().getString("key1"))).commit();
        } else {
            Intent myIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    myIntent,
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            number++;
            SharedPreferences.Editor editor = context.getSharedPreferences("Session", context.MODE_PRIVATE).edit();
            editor.putInt("NotificationCount", number).commit();
            myNotification = new NotificationCompat.Builder(context)
                    .setContentTitle(number + " yeni Bildirim!")
                    .setContentText("Do Something...")
                    .setTicker("Notification!")
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.notification)
                    .build();
            editor.putInt("NotificationId", 0).commit();
        }

        notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
    }
}