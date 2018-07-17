package com.flashsales;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.dao.DBViewedProducts;
import com.flashsales.dao.UserDao;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;
import com.flashsales.datamodel.User;
import com.flashsales.fragments.FragmentAccount;
import com.flashsales.fragments.FragmentAccountEdit;
import com.flashsales.fragments.FragmentViewedProducts;
import com.google.android.gms.common.api.GoogleApiClient;


import com.flashsales.fragments.FragmentLogin;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity implements FragmentLogin.EventLogin
        , FragmentViewedProducts.OnViwedProductEvent
        , FragmentAccount.OnAccountUpdate
        , FragmentAccountEdit.OnAccountEditSaved {

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
    private ArrayList<Product> viewedProducts;
    private FragmentAccount fragmentAccount;
    private FragmentViewedProducts fragmentViewedProducts;
    private FragmentAccountEdit fragmentEdit;
    private FrameLayout frameAccount,frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        prefs = new SharedPreferenceUtils(this);
        user = prefs.getUser();
        cartCount = prefs.getCartCount();
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.mi_cart);
        menuItem.setIcon(ConverterCartBadge.convertLayoutToImage(this, cartCount, R.drawable.ic_shopping_cart));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //overridePendingTransition(R.anim.right_to_left,R.anim.left_to_right);
                break;
            case R.id.mi_cart:
                loadCart();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
      /*  if (prefs.getTimeoutMills() > System.currentTimeMillis() && System.currentTimeMillis() != 0) {
            prefs.setCartCount(0);
        }*/
        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
    }

    @Override
    protected void onStart() {
        super.onStart();
     /*   if (prefs.getTimeoutMills() > System.currentTimeMillis() && System.currentTimeMillis() != 0) {
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

         frame = (FrameLayout) findViewById(R.id.content_frame);
         frameAccount = (FrameLayout) findViewById(R.id.content_frame_account);

        if (user == null) {
            fragmentLogin = FragmentLogin.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragmentLogin).commit();
        } else {
            ProductsLoader loader = new ProductsLoader();
            loader.execute();
            inflateAccountDetails();
        }

    }

    private void inflateAccountDetails() {
        if (user == null)
            return;
        fragmentAccount = FragmentAccount.newInstance(user);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame_account, fragmentAccount).commit();
    }

    private void hideAccontDetails() {
        if (fragmentAccount != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentAccount);
        }
    }

    private void inflateFragmentViewed() {
        if (viewedProducts == null)
            return;
        fragmentViewedProducts = FragmentViewedProducts.newInstance(viewedProducts);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragmentViewedProducts).commit();
    }

    private void hideFragmentViewed() {
        if (fragmentViewedProducts != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentViewedProducts).commit();
        }
    }

    private void inflateEdit() {
        if(user == null)
            return;
        fragmentEdit = FragmentAccountEdit.newInstance(user);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragmentEdit).commit();
    }

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

    private void intentViewProduct(Product product) {

        ProductDisplay productDisplay = new ProductDisplay();
        productDisplay.setName(product.getName());
        productDisplay.setBrand(product.getBrand());
        productDisplay.setRetailPrice(Double.parseDouble(product.getRetailPrice()));
        productDisplay.setPrice(Double.parseDouble(product.getPrice()));
        productDisplay.setShortName(product.getShortName());

        Intent intent = new Intent(AccountActivity.this, ProductActivity.class);
        intent.putExtra(ProductActivity.KEY_PRODUCT, productDisplay);
        startActivity(intent);


    }

    private void updateUser(){
        Snackbar bar  = Snackbar.make(frame,getString(R.string.updating_account),Snackbar.LENGTH_SHORT);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(this);
        contentLay.addView(item);
        bar.show();

        UserDao userDao = new UserDao();
        userDao.updateUser(this,this.user);
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
    public void onViewClicked(Product product) {
        intentViewProduct(product);
    }

    @Override
    public void onAccountEdit() {
        hideFragmentViewed();
        inflateEdit();
    }

    @Override
    public void onSaveAccount(User userUpdated) {
        hideEdit();
        inflateFragmentViewed();
        user = userUpdated;
        updateUser();
    }

    @Override
    public void onCancel() {
        hideEdit();
        inflateFragmentViewed();
    }

    private class ProductsLoader extends AsyncTask<Void, Void, ArrayList<Product>> {


        private ProgressAlert progressAlert;
        private DBViewedProducts dbViewedProducts = new DBViewedProducts(AccountActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressAlert = new ProgressAlert(AccountActivity.this, "loadin viewd products", "message");
        }


        @Override
        protected ArrayList<Product> doInBackground(Void... voids) {

            ArrayList<Product> products = dbViewedProducts.getViewedProducts();

            return products;
        }

        @Override
        protected void onPostExecute(ArrayList<Product> aVoid) {
            super.onPostExecute(aVoid);
            progressAlert.stopAlert();
            viewedProducts = aVoid;
            inflateFragmentViewed();
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
