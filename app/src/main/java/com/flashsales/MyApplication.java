package com.flashsales;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;


import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;
import com.flashsales.datamodel.Review;
import com.flashsales.datamodel.ReviewFactory;
import com.flashsales.datamodel.User;

public class MyApplication extends Application {
    private static MyApplication singleton;

    // private ProductsViewedDbHelper cartDbHelper;

    private ArrayList<ProductDisplay> displayArrayList;
    private ArrayList<ProductDisplay> topDisplayProductSales;
    private ArrayList<Review> reviews;
    private User user;
    //private CartProduct cartProduct;

    public static MyApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        singleton = this;
        //cartDbHelper = new ProductsViewedDbHelper(this);
        // cartList = cartDbHelper.getCartProducts();
        // user = new User().getInstance();
        ReviewFactory reviewFactory = ReviewFactory.getInstance();
        reviewFactory.createReviews();
        reviews = new ArrayList<>();
        reviews.addAll(reviewFactory.getReviewArrayList());

       /* SharedPreferenceUtils prefs = new SharedPreferenceUtils(this);
        if (prefs.getTimeoutMills() < System.currentTimeMillis() && prefs.getTimeoutMills() != 0) {
            prefs.setCartCount(0);
        }*/


       printHashKey(this);
       hashFromSHA1("6D:F6:1E:4E:49:D0:39:C0:57:A9:53:63:83:CE:B1:AE:83:F1:E6:BF");
    }

    public void hashFromSHA1(String sha1) {
        String[] arr = sha1.split(":");
        byte[] byteArr = new  byte[arr.length];

        for (int i = 0; i< arr.length; i++) {
            byteArr[i] = Integer.decode("0x" + arr[i]).byteValue();
        }

        Log.e("hash : ", Base64.encodeToString(byteArr, Base64.NO_WRAP));
    }


    public static void printHashKey(Context context) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("AppLog", "key:" + hashKey + "=");
            }
        } catch (Exception e) {
            Log.e("AppLog", "error:", e);
        }
    }

    /*public void updateList() {
        if (cartDbHelper == null)
            cartDbHelper = new ProductsViewedDbHelper(this);
        cartList = cartDbHelper.getCartProducts();
    }*/


    public User getUser() {
        return user;
    }

    public void setDisplayArrayList(ArrayList<ProductDisplay> displayList) {
        if (this.displayArrayList == null)
            displayArrayList = new ArrayList<>();
        displayArrayList.clear();
        displayArrayList.addAll(displayList);
        setTopDisplayProductSales();
    }

    public void setTopDisplayProductSales(){
     if(topDisplayProductSales== null)
         topDisplayProductSales = new ArrayList<>();
      topDisplayProductSales.clear();

      for(int i=0;i<displayArrayList.size();i++){
          //Filter $5 price
          if(displayArrayList.get(i).getPrice()>5){
              topDisplayProductSales.add(displayArrayList.get(i));
          }
      }
    }

    public ArrayList<ProductDisplay> getTopDisplayProductSales() {
        return topDisplayProductSales;
    }

    public void setListenerNetwork(NetworkChangeReciever.NetworkListener mlistener) {
        NetworkChangeReciever.mListener = mlistener;
    }

    public ArrayList<ProductDisplay> getDisplayArrayList() {
        return displayArrayList;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }


    public void reorderReviews() {
        Collections.shuffle(reviews);
    }
}
