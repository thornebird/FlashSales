package com.flashsales.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConfig {
    public NetworkConfig(){}


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
}
