package com.flashsales;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.flashsales.Utils.Configs;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public final static String KEY_NAME = "name";
    public final static String KEY_BRAND = "brand";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String brand = "";
        String name = "";


        for (String key : remoteMessage.getData().keySet()) {
            if (key.equals(KEY_BRAND)) {
                brand = remoteMessage.getData().get(KEY_BRAND);
            } else if (key.equals(KEY_NAME)) {
                name = remoteMessage.getData().get(KEY_NAME);
            }
        }
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(Configs.KEY_PRODUCT_NOTI_NAME,name);
        intent.putExtra(Configs.KEY_PRODUCT_NOTI_BRAND,brand);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String channelID = Configs.KEY_NOTI_CHANEL_NAME;
        if (BuildConfig.DEBUG) {
            channelID =  Configs.KEY_NOTI_CHANEL_NAME_DEBUG;
        }


        int importance = NotificationManager.IMPORTANCE_HIGH;

        AudioAttributes.Builder attrs = new AudioAttributes.Builder();
        attrs.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
        attrs.setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT);

        NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this, channelID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelID,Configs.KEY_NOTI_CHANEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationChannel.setSound(uri, attrs.build());
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            notBuilder.setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setContentIntent(pendingIntent);

            int notificationId = new Random().nextInt(60000);
            notificationManager.notify(notificationId, notBuilder.build());
        }
    }
}
