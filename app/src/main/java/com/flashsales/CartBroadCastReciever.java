package com.flashsales;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import com.flashsales.CartActivity;
import com.flashsales.R;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.Utils.Utils;

public class CartBroadCastReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentOpen = new Intent(context, CartActivity.class);
         PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intentOpen,
                0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_account_circle)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(context.getString(R.string.noti_claim_savings))
                .setContentText(context.getString(R.string.noti_shopping_cart_expire))
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

    }
}
