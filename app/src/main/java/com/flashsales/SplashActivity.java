package com.flashsales;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import java.util.ArrayList;


import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.dao.DBViewedProducts;
import com.flashsales.dao.ProductApi;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;

import com.flashsales.datamodel.User;

public class SplashActivity extends AppCompatActivity implements ProductApi.OnLoaded {

    private MyApplication myApplication;
    private User user;
    private ProductApi productApi;
    private SharedPreferenceUtils prefs;
    private ArrayList<ProductDisplay> productDisplays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_splash);

        prefs = new SharedPreferenceUtils(this);
        user = prefs.getUser();
        myApplication = (MyApplication) getApplication();

        loadProducts();
        init();
    }

  /*  @Override
    protected void onResume() {
        super.onResume();
        if (prefs != null && prefs.getTimeoutMills() < System.currentTimeMillis() && prefs.getTimeoutMills() != 0) {
            prefs.setCartCount(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (prefs.getTimeoutMills() > System.currentTimeMillis() && System.currentTimeMillis() != 0) {
            prefs.setCartCount(0);
        }
    }*/

    private void init() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
    }


    public void loadProducts() {
        productApi = ProductApi.getInstance();
        productApi.okHttpRequestProducts();
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

        if (user == null) {
            Intent intent = new Intent(SplashActivity.this, AccountActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onProductLoaded(Product productObject) {

    }

    @Override
    public void onProductsLoaded(ArrayList<ProductDisplay> productDisplays) {

        myApplication.setDisplayArrayList(productDisplays);
        checkViewedProducts(productDisplays);
        /*if (user == null) {
            Intent intent = new Intent(SplashActivity.this, AccountActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/
        //overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
        // split if logged in go to login else go to mainactivity

    }

    @Override
    public void onCartAdded(CartProduct cartProduct) {

    }

    @Override
    public void onCartDelted(CartProduct cartProduct) {

    }

    @Override
    public void onCartLoaded(CartProduct cartProduct) {
        /*myApplication.setCartProduct(cartProduct);*/

    }

    @Override
    public void onCartLoadedEmpty() {

    }
}
