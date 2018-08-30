package com.flashsales;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.List;

public class NetworkChangeReciever extends BroadcastReceiver {
    private Context context;
    public static NetworkListener mListener;
/*    public NetworkChangeReciever(NetworkListener networkListener){
        this.mListener = networkListener;
    }*/

    public NetworkChangeReciever() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        for (int i=0;i<4;i++) {
            if (!hasNetworkConnection(context)) {
                if (mListener != null &&i==3)
                    mListener.networkUnavailable();
            } else {
                if (mListener != null)
                    mListener.networkAvailable();
            }
        }
    }

    public boolean hasNetworkConnection(Context context) {
        boolean isConnectedWifi = false;
        boolean isConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();

        for (NetworkInfo ni : networkInfos) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected())
                    isConnectedWifi = true;
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected())
                    isConnectedMobile = true;
            }
        }
        return isConnectedMobile || isConnectedWifi;
    }


    public interface NetworkListener {
        public void networkAvailable();
        public void networkUnavailable();
    }
}
