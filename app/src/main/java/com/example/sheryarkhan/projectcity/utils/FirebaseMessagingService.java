package com.example.sheryarkhan.projectcity.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.sheryarkhan.projectcity.R;
import com.example.sheryarkhan.projectcity.activities.NotificationPostActivity;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Sheryar Khan on 10/14/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    public FirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        if (remoteMessage.getData().size() > 0) {
//
//        }
        if (remoteMessage.getData() != null) {
            if(remoteMessage.getData().get("type").equals(getString(R.string.postLike))){

                String title = remoteMessage.getData().get("title");
                String message = remoteMessage.getData().get("body");
                String postId = remoteMessage.getData().get("postId");

                sendNotification(title,message,postId);

            }else if(remoteMessage.getData().get("type").equals(getString(R.string.postComment))){
                String title = remoteMessage.getData().get("title");
                String message = remoteMessage.getData().get("body");
                String postId = remoteMessage.getData().get("postId");

                sendNotification(title,message,postId);
            }else if (remoteMessage.getData().get("type").equals(getString(R.string.postCommentLike))){

            }else if(remoteMessage.getData().get("type").equals(getString(R.string.postTown))) {
                String title = remoteMessage.getData().get("title");
                String message = remoteMessage.getData().get("body");
                String postId = remoteMessage.getData().get("postId");

                sendNotification(title,message,postId);
            }

        }


        //super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }


    private void sendNotification(String title, String message,String postId) {
        Intent intent = new Intent(this, NotificationPostActivity.class);
        intent.putExtra("postId",postId);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Bitmap bitmapIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.projectcityicon);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(bitmapIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
    }
}
