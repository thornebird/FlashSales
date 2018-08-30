package com.flashsales;

import com.flashsales.Utils.Configs;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    public MyFirebaseInstanceIDService(){}

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String channelID = Configs.KEY_NOTI_CHANELID;
        if (BuildConfig.DEBUG) {
            channelID =  Configs.KEY_NOTI_CHANEL_NAME_DEBUG;
        }
        FirebaseMessaging.getInstance().subscribeToTopic(channelID);
        String token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferenceUtils prefs = new SharedPreferenceUtils(this);
        prefs.setFirebaseToken(token);
    }
}
