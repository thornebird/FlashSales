package com.flashsales;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.Utils.Utils;
import com.flashsales.datamodel.User;
import com.flashsales.fragments.FragmentAccountEdit;
import com.google.android.gms.common.api.GoogleApiClient;


import com.flashsales.fragments.FragmentLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class AccountActivity extends AppCompatActivity implements FragmentLogin.EventLogin
        , FragmentAccountEdit.OnAccountEditSaved
        , NetworkChangeReciever.NetworkListener {

    private String name;
    private String email;
    private String gender;
    private String id;
    private int age;
    private String birthday;
    private FragmentLogin fragmentLogin;
    private GoogleApiClient mGoogleApiClient;
    private User user;
    int cartCount;
    SharedPreferenceUtils prefs;
    private MyApplication mApplication;
    private boolean errorInflated = false;
    private FragmentAccountEdit fragmentEdit;
    private FrameLayout frameAccount, frame;
    private ErrorAlert errorAlert;
    private String promotiontext;
    public final static String KEY_PROMOTION_TEXT = "keyPromotionText";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        if(getIntent().getExtras()!=null){
            promotiontext = getIntent().getExtras().getString(KEY_PROMOTION_TEXT);
        }
        mApplication = (MyApplication) getApplication();
        mApplication.setListenerNetwork(this);
        prefs = new SharedPreferenceUtils(this);
        user = prefs.getUser();
        cartCount = prefs.getCartCount();

        if (!prefs.getUser().isLoggedIn()) {
            cartCount = 0;
        }
      //  setConfig();
        init();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.mi_cart);
        if (!user.isLoggedIn()) {
            cartCount = 0;
        }
        if (user.isLoggedIn()) {
            menuItem.setIcon(ConverterCartBadge.convertLayoutToImage(this, cartCount, R.drawable.ic_shopping_cart));
        }else{
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.mi_cart:
                if (user.isLoggedIn()) {
                    loadCart();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!user.isLoggedIn()) {
            finishAffinity();
            System.exit(0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mApplication == null)
            mApplication = MyApplication.getInstance();
        mApplication.setListenerNetwork(this);
      /*  if (prefs.getTimeoutMills() > System.currentTimeMillis() && System.currentTimeMillis() != 0) {
            prefs.setCartCount(0);
        }*/
        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
    }


    private void init() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.account_flash_sales));

        frame = (FrameLayout) findViewById(R.id.content_frame);
        inflateLogin();
        /*  if (user == null) {*/



       /* } else {
            inflateEdit();
        }*/
    }

    private void inflateLogin(){
        if (!isFinishing()) {
            fragmentLogin = FragmentLogin.newInstance(promotiontext);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragmentLogin).commit();
        }
    }

   /* private void setConfig() {
        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        promotiontext = mFirebaseRemoteConfig.getString(Utils.CONFIG_PROMOTION_TEXT);

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            promotiontext = mFirebaseRemoteConfig.getString(Utils.CONFIG_PROMOTION_TEXT);
                            inflateLogin();
                            //promotiontext = mFirebaseRemoteConfig.getString(Utils.CONFIG_PROMOTION_TEXT);
                         *//*   Toast.makeText(AccountActivity.this, "Fetch Succeeded "+ promotiontext,
                                    Toast.LENGTH_SHORT).show();*//*

                          *//*  if (!promotiontext.equals("")) {
                              *//**//*  fragmentLogin.updatePromotionText(promotiontext);*//**//*
                            }*//*

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        }

                        *//*else {
                            Toast.makeText(AccountActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }*//*
                        // displayWelcomeMessage();
                    }
                });
    }*/


 /*   private void inflateEdit() {
        if(user == null)
            return;
        fragmentEdit = FragmentAccountEdit.newInstance(user);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragmentEdit).commit();
    }*/

    private void hideEdit() {
        if (fragmentEdit != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentEdit).commit();
        }
    }


    private void loadCart() {
        Intent intent = new Intent(AccountActivity.this, CartActivity.class);
        startActivity(intent);
    }


    private void updateUser() {
        Snackbar bar = Snackbar.make(frame, getString(R.string.updating_account), Snackbar.LENGTH_SHORT);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(this);
        contentLay.addView(item);
        bar.show();
/*
        UserDao userDao = new UserDao();
        *//*userDao.updateUser(this, this.user);*/
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragmentLogin != null)
            fragmentLogin.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void OnLogin(User user) {

        prefs.saveLogins(user);
        Intent intent = new Intent(AccountActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public void onSaveAccount(User userUpdated) {
        user = userUpdated;
        updateUser();

    }

    @Override
    public void networkAvailable() {
        hideInternetError();
    }

    @Override
    public void networkUnavailable() {
        showInternetError();

    }

    private void showInternetError() {
        if (!errorInflated) {
            errorAlert = new ErrorAlert(this);
            errorInflated = !errorInflated;
        }
    }

    private void hideInternetError() {
        if (errorAlert != null && errorInflated) {
            errorAlert.stopDialog();
            errorInflated = !errorInflated;
        }
    }


}
