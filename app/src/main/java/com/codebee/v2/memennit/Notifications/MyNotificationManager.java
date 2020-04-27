package com.codebee.v2.memennit.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.codebee.v2.memennit.R;

public class MyNotificationManager {

    private Context ctx;
    private static final int NOTIFICATION_ID = 1;
    private String channelId = "channel-01";
    private String channelName = "Channel Name";
    int importance = NotificationManager.IMPORTANCE_HIGH;

    public MyNotificationManager(Context ctx) {
        this.ctx = ctx;
    }

    public void showNotification(String from, String notification, Intent intent){

        PendingIntent pendingIntent = PendingIntent.getActivity(
                ctx,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx,channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(from)
                .setContentText(notification)
                .setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID,builder.build());

    }
}
