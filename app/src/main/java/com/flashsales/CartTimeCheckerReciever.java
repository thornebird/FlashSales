package com.flashsales;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import com.flashsales.Utils.SharedPreferenceUtils;

import static android.content.Context.ALARM_SERVICE;

public class CartTimeCheckerReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("cartchecker", "called");
        SharedPreferenceUtils predfs = new SharedPreferenceUtils(context);
        long mills = predfs.getTimeoutMills();
        if (System.currentTimeMillis() > mills) {
            predfs.setCartCount(0);
            Toast.makeText(context, "System.currentTimeMillis()>mills", Toast.LENGTH_SHORT).show();
        } else {
             setPrizeAlaram(context);
        }
    }

    private void setPrizeAlaram(Context context) {
        Intent intent = new Intent(context, PrizeBroadcastReciever.class);
        intent.putExtra("requestCode", 0);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3000, pendingIntent);
    }
}
