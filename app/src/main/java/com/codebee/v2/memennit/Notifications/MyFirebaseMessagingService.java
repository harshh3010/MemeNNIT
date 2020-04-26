package com.codebee.v2.memennit.Notifications;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.codebee.v2.memennit.PostActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        notifyUser(remoteMessage.getFrom(),remoteMessage.getNotification().getBody());
    }

    public void notifyUser(String from,String notification){
        MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
        myNotificationManager.showNotification(from , notification, new Intent(getApplicationContext(), PostActivity.class));
    }

    //improve this code
}
