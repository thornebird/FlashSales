package com.flashsales.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.flashsales.datamodel.ShippingAddress;
import com.flashsales.datamodel.User;

import java.io.UTFDataFormatException;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceUtils {

    private Context context;

    public SharedPreferenceUtils(Context context) {
        this.context = context;
    }

    public void saveLogins(User user) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(Utils.MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Utils.keyName, user.getName());
        editor.putString(Utils.keyEmail, user.getEmail());
        editor.putString(Utils.keyPass, user.getPassword());
        editor.putString(Utils.keyImage, user.getImageFb());
        editor.commit();
    }

    public User getUser() {
        User user = null;
        SharedPreferences userPrefs = context.getSharedPreferences(Utils.MyPREFERENCES, MODE_PRIVATE);
        String name = userPrefs.getString(Utils.keyName, "");
        String email = userPrefs.getString(Utils.keyEmail, "");
        String pass = userPrefs.getString(Utils.keyPass, "");
        String imageFb = userPrefs.getString(Utils.keyImage, "");
        if (!name.equals("") && !email.equals("")) {
            user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(pass);
            user.setImageFb(imageFb);
        }
        return user;
    }

    public void setCartToken(String cartToken) {
        SharedPreferences userPrefs = context.getSharedPreferences(Utils.MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putString(Utils.keyCartToken, cartToken);
        editor.commit();
    }

    public String getCartToken() {
        SharedPreferences userPrefs = context.getSharedPreferences(Utils.MyPREFERENCES, MODE_PRIVATE);
        String token = userPrefs.getString(Utils.keyCartToken, "");
        return token;
    }


    public void saveAddress(ShippingAddress address) {
        SharedPreferences preferences = context.getSharedPreferences(Utils.MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Utils.keyTelephone, address.getTelephone());
        editor.putString(Utils.keyShippingAddress, address.getShippingAddress());
        editor.putString(Utils.keyApt, address.getApt());
        editor.putString(Utils.keyCity, address.getCity());
        editor.putString(Utils.keyProvince, address.getProvince());
        editor.putString(Utils.keyZipCode, address.getCountry());
        editor.putString(Utils.keyCountry, address.getCountry());
        editor.commit();
    }

    public ShippingAddress getAddress() {
        ShippingAddress address = new ShippingAddress();
        SharedPreferences prefs = context.getSharedPreferences(Utils.MyPREFERENCES, MODE_PRIVATE);
        address.setTelephone(prefs.getString(Utils.keyTelephone, ""));
        address.setShippingAddress(prefs.getString(Utils.keyShippingAddress, ""));
        address.setApt(prefs.getString(Utils.keyApt, ""));
        address.setCity(prefs.getString(Utils.keyCity,""));
        address.setProvince(prefs.getString(Utils.keyProvince,""));
        address.setZipCode(prefs.getString(Utils.keyZipCode,""));
        address.setCountry(prefs.getString(Utils.keyCountry,""));
        return address;
    }

    public void setCartCount(int count){
        SharedPreferences preferences= context.getSharedPreferences(Utils.MyPREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Utils.keyCartCount,count);
        editor.commit();
    }

    public int getCartCount(){
        SharedPreferences preferences = context.getSharedPreferences(Utils.MyPREFERENCES,MODE_PRIVATE);
      return   preferences.getInt(Utils.keyCartCount,0);
    }

    public void setTimeOut(long timeoutMills){
        SharedPreferences prefs = context.getSharedPreferences(Utils.MyPREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(Utils.keyTimeout,timeoutMills);
        editor.commit();
    }

    public long getTimeoutMills(){
        SharedPreferences preferences = context.getSharedPreferences(Utils.MyPREFERENCES,MODE_PRIVATE);
        return preferences.getLong(Utils.keyTimeout,0);
    }

    public void saveFirebaseId(String id){
        SharedPreferences prefs  = context.getSharedPreferences(Utils.MyPREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Utils.keyFirebase,id);
        editor.commit();
    }

    public String getFirebaseId(){
        SharedPreferences prefs =  context.getSharedPreferences(Utils.MyPREFERENCES,MODE_PRIVATE);
        return prefs.getString(Utils.keyFirebase,"");
    }
}
