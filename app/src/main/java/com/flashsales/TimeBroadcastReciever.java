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

import com.flashsales.Utils.SharedPreferenceUtils;

public class TimeBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferenceUtils prefs = new SharedPreferenceUtils(context);
        prefs.setCartCount(0);
        Intent intentShop = new Intent(context, MainActivity.class);
        PendingIntent pendingintentShop = PendingIntent.getActivity(context,0,intentShop,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_account_circle)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(context.getString(R.string.noti_cart_expired))
                .setContentText(context.getString(R.string.noti_countinue_shopping))
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .addAction(R.drawable.ic_ondemand_video,context.getString(R.string.yes),pendingintentShop)
                .setContentIntent(pendingintentShop);

        NotificationManager manager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());
    }


}
