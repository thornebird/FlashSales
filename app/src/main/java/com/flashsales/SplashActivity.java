package com.flashsales;

import android.content.Intent;
import android.net.Uri;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.firebase.client.Firebase;
import com.flashsales.Utils.Configs;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.Utils.Utils;
import com.flashsales.dao.DBViewedProducts;
import com.flashsales.dao.ProductApi;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;

import com.flashsales.datamodel.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import bolts.AppLinks;


public class SplashActivity extends AppCompatActivity implements ProductApi.OnLoaded, NetworkChangeReciever.NetworkListener {

    private MyApplication myApplication;
    private User user;
    private ProductApi productApi;
    private SharedPreferenceUtils prefs;
    private ArrayList<ProductDisplay> setList;
    private ErrorAlert errorAlert;
    private boolean errorInflated = false;
    private boolean isDeeplink = false;
    private boolean isNotification = false;
    private ProductDisplay sendProduct;
    private int errorCount = 0;
    private String promotiontext;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getIntent().getExtras() != null) {
            String name = String.valueOf(getIntent().getExtras().get(Configs.KEY_PRODUCT_NOTI_NAME));
            String brand = String.valueOf(getIntent().getExtras().get(Configs.KEY_PRODUCT_NOTI_BRAND));
            if (!name.equals("null") && !brand.equals("null")) {
                isNotification = true;
                loadProductIncoming(name, brand);
                Log.i("FCM 2", name + " " + brand);
            }
        }

        constraintLayout = (ConstraintLayout) findViewById(R.id.frame);
        setList = new ArrayList<>();

       /* mReferrerClient = InstallReferrerClient.newBuilder(this).build();
        mReferrerClient.startConnection(this);
*/


        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            isDeeplink = true;
            final String productName = targetUrl.getQueryParameter(Configs.KEY_PRODUCT_NAME_FB_ADDS);
            final String brand = targetUrl.getQueryParameter(Configs.KEY_PRODUCT_BRAND_FB_ADDS);
            loadProductIncoming(productName, brand);
        }

        AppLinkData.createFromActivity(this);
        AppLinkData.fetchDeferredAppLinkData(getApplicationContext(), getString(R.string.facebook_app_id),
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        if (appLinkData != null) {
                            Bundle bundle = appLinkData.getArgumentBundle();
                            Uri link = appLinkData.getTargetUri();
                            saveToFirbase("fetchDeferredAppLinkData:" + link.toString());
                        }
                    }
                });


        prefs = new SharedPreferenceUtils(this);
        user = prefs.getUser();

        myApplication = (MyApplication) getApplication();
        myApplication.setListenerNetwork(this);
        if (!isDeeplink && !isNotification) {
            loadProducts();
        }
    }

    private void loadProductIncoming(final String productName, final String brand) {
        final ProgressAlert alert = new ProgressAlert(SplashActivity.this, getBaseContext().getString(R.string.loading), productName);
        sendProduct = new ProductDisplay();
        final ProductApi productApi = ProductApi.getInstance();
        productApi.setListener(this);
        productApi.okhttpRequestProduct(SplashActivity.this, brand, productName);
        productApi.setListener(new ProductApi.OnLoaded() {
            @Override
            public void onProductLoaded(Product productDemo) {
                if (productDemo != null) {
                    sendProduct.setShortName(productDemo.getShortName());
                    sendProduct.setName(productDemo.getName());
                    sendProduct.setBrand(productDemo.getBrand());
                    sendProduct.setImage(productDemo.getImage());
                    sendProduct.setRetailPrice(Double.valueOf(productDemo.getRetailPrice()));
                    sendProduct.setPrice(Double.valueOf(productDemo.getPrice()));
                    sendProduct.setFree(true);

                    alert.stopAlert();

                    Intent intent = new Intent(SplashActivity.this, ProductActivity.class);
                    intent.putExtra(ProductActivity.KEY_PRODUCT, sendProduct);
                    startActivity(intent);
                    finish();
                } else {
                    errorCount++;
                    if (errorCount == 4) {

                        //  productApi.okhttpRequestProduct(SplashActivity.this, brand, productName);
                        loadProducts();

                       /* Snackbar bar = Snackbar.make(constraintLayout, getBaseContext().getString(R.string.loading_error_patient), Snackbar.LENGTH_SHORT);
                        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
                        ProgressBar item = new ProgressBar(SplashActivity.this);
                        contentLay.addView(item);
                        bar.show();*/
                    } else {
                        productApi.okhttpRequestProduct(SplashActivity.this, brand, productName);
                        Snackbar bar = Snackbar.make(constraintLayout, getBaseContext().getString(R.string.loading_error_patient), Snackbar.LENGTH_SHORT);
                        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
                        ProgressBar item = new ProgressBar(SplashActivity.this);
                        contentLay.addView(item);
                        bar.show();
                    }
                }
            }

            @Override
            public void onProductsLoaded(ArrayList<ProductDisplay> productDisplays) {

            }

            @Override
            public void onCartAdded(CartProduct cartProduct) {

            }

            @Override
            public void onCartDelted(CartProduct cartProduct) {

            }

            @Override
            public void onCartLoaded(CartProduct cartProduct) {

            }

            @Override
            public void onCartLoadedEmpty() {

            }

            @Override
            public void onCheckoutCartEmptied() {

            }
        });
    }

    private void checkCart() {
        long timeOut = prefs.getTimeoutMills();
        int cartCount = prefs.getCartCount();
        if (cartCount != 0 && timeOut != 0 && timeOut < System.currentTimeMillis()) {
            prefs.setCartCount(0);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myApplication != null)
            myApplication.setListenerNetwork(this);
        checkCart();
    }
/*

    @Override
    protected void onStart() {
        super.onStart();
        checkCart();
    }
*/
/*
    private void init() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
    }*/


    public void loadProducts() {
        productApi = ProductApi.getInstance();
        productApi.okHttpRequestProducts(this);
        productApi.setListener(this);
    }

    private void checkViewedProducts(ArrayList<ProductDisplay> displayArrayList) {
        DBViewedProducts dbViewedProducts = new DBViewedProducts(this);
        ArrayList<Product> viewedProducts = dbViewedProducts.getViewedProducts();
        for (int i = 0; i < viewedProducts.size(); i++) {
            Product product = viewedProducts.get(i);
            boolean isExist = false;

            for (int ii = 0; ii < displayArrayList.size(); ii++) {
                ProductDisplay productDisplay = displayArrayList.get(ii);
                if (product.getName().equals(productDisplay.getName()) &&
                        product.getBrand().equals(productDisplay.getBrand())) {
                    dbViewedProducts.upDateProduct(product);
                    isExist = true;
                    ii++;
                } else if (!isExist && ii == displayArrayList.size() - 1) {
                    dbViewedProducts.deleteProduct(product);
                }
            }
        }

        if (user == null || !user.isLoggedIn()) {


            final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                 /*   .setDeveloperModeEnabled(BuildConfig.DEBUG)*/
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
                                mFirebaseRemoteConfig.activateFetched();
                            }
                            intentAccount();

                        }

                    });
        } else {
        prefs.saveFirstTime(false);
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}

    private void intentAccount() {
        Intent intent = new Intent(SplashActivity.this, AccountActivity.class);
        intent.putExtra(AccountActivity.KEY_PROMOTION_TEXT, promotiontext);
        startActivity(intent);
        finish();
    }

    @Override
    public void onProductLoaded(Product productObject) {

    }

    @Override
    public void onProductsLoaded(ArrayList<ProductDisplay> productDisplays) {
        setList = productDisplays;
        createFakeProductRating(setList);
        myApplication.setDisplayArrayList(setList);
        checkViewedProducts(productDisplays);
    }

    @Override
    public void onCartAdded(CartProduct cartProduct) {

    }

    @Override
    public void onCartDelted(CartProduct cartProduct) {

    }

    @Override
    public void onCartLoaded(CartProduct cartProduct) {
    }

    @Override
    public void onCartLoadedEmpty() {

    }

    @Override
    public void onCheckoutCartEmptied() {

    }

    @Override
    public void networkAvailable() {
        if (setList.size() == 0)
            loadProducts();
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

    private void createFakeProductRating(ArrayList<ProductDisplay> productDisplays) {
        for (int i = 0; i < productDisplays.size(); i++) {
            double rating = ThreadLocalRandom.current().nextDouble(3, 5);
            productDisplays.get(i).setFakeRating(rating);
        }
    }


    private void hideInternetError() {
        if (errorAlert != null && errorInflated) {
            errorAlert.stopDialog();
            errorInflated = !errorInflated;
        }
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */


/*@Override
    public void onInstallReferrerSetupFinished(int responseCode) {
        switch (responseCode) {
            case InstallReferrerClient.InstallReferrerResponse.OK:
                try {
                    ReferrerDetails response = mReferrerClient.getInstallReferrer();
                    Log.v("InstallReferrerClient", "InstallReferrer conneceted");

                    Log.v("Install response", response.getInstallReferrer());
                    saveToFirbase(response.getInstallReferrer());
                    Toast.makeText(this, response.getInstallReferrer(), Toast.LENGTH_LONG).show();
                    //handleReferrer(response);
                    mReferrerClient.endConnection();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                Log.w("InstallReferrerClient", "InstallReferrer not supported");
                break;
            case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                Log.w("InstallReferrerClient", "Unable to connect to the service");
                break;
            default:
                Log.w("InstallReferrerClient", "responseCode not found.");
        }
    }
*/
    private void saveToFirbase(String referrer) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("referrers");

        String key = db.push().getKey();
        db.child(key).setValue(referrer);
        db.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

