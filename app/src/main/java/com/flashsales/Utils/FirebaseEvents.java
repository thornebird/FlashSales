package com.flashsales.Utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseEvents {

   private FirebaseAnalytics fbAnalytics;

    public FirebaseEvents(Context context){
        fbAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void logEvent(String id,String name,String loginMethod){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, loginMethod);
        fbAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    public void atcEvent(String productName,String productBrand,String count){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productName);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productBrand);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, count);
        fbAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }

    public  void  checkoutEvent(String productName,String productBrand,String amount){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productName);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productBrand);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, amount);
        fbAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle);
    }

    public void vcEvent(String productName,String productBrand,String price){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productName);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productBrand);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, price);
        fbAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    public void purchaseEvent(String idPayment,String cartCount,String amount){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, idPayment);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, cartCount);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, amount);
        fbAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle);
    }
}
