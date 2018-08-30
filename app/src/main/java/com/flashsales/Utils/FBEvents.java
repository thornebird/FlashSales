package com.flashsales.Utils;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FBEvents {

    private AppEventsLogger logger;

    public FBEvents(Application application) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        logger = AppEventsLogger.newLogger(application);
        logger.activateApp(application);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logCompletedRegistrationEvent(String registrationMethod) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, registrationMethod);
        logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logAddedToCartEvent(String contentData, String contentId, String contentType, String currency, double price) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, contentData);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, price, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logAddedToWishlistEvent(String contentData, String contentId, String contentType, String currency, double price) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, contentData);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST, price, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logInitiatedCheckoutEvent(String contentData, String contentId, String contentType, int numItems, boolean paymentInfoAvailable, String currency, double totalPrice) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, contentData);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, numItems);
        params.putInt(AppEventsConstants.EVENT_PARAM_PAYMENT_INFO_AVAILABLE, paymentInfoAvailable ? 1 : 0);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, totalPrice, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logViewedContentEvent (String contentType, String contentData, String contentId, String currency, double price) {
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, contentData);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, price, params);
    }

   public void logPurchase(String mCurrency,String mProductType,String mPurchaseID,double valueToSum){
       Bundle parameters = new Bundle();
       parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, mCurrency);
       parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, mProductType);
       parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, mPurchaseID);
        //this is how the price looks like
       logger.logEvent(AppEventsConstants.EVENT_NAME_PURCHASED, valueToSum, parameters);
    }

}
