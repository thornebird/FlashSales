package com.flashsales;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.dao.ProductApi;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;
import com.flashsales.datamodel.ShippingAddress;
import com.flashsales.fragments.FragmentCart;
import com.flashsales.fragments.FragmentShipping;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements ProductApi.OnLoaded,
        FragmentCart.OnCartEvent,
        FragmentShipping.OnAddressEvent {

    private FragmentCart fragmentCart;
    private FragmentShipping fragmentShipping;
    private ProductApi productApi;
    private CartProduct cartProduct;
    private boolean cartActivce = false;
    private boolean cartEmpty;
    private int cartCount;
    private SharedPreferenceUtils prefs;
    private FrameLayout contentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        prefs = new SharedPreferenceUtils(this);
        cartCount = prefs.getCartCount();
        productApi = ProductApi.getInstance();
        productApi.setListener(this);
        productApi.viewCart(this);
        initViews();
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
      /*  if (prefs != null) {
            if(prefs.getTimeoutMills()<System.currentTimeMillis()&&prefs.getTimeoutMills()!=0)
                prefs.setCartCount(0);
        }*/
        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
    }

    @Override
    protected void onStart() {
        super.onStart();
    /*    if (prefs != null) {
            if(prefs.getTimeoutMills()<System.currentTimeMillis()&&prefs.getTimeoutMills()!=0)
                prefs.setCartCount(0);
        }*/
        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
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
                if (cartActivce) {
                    this.finish();
                } else {
                    onBackPressed();
                }
//                 onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (cartActivce) {
            this.finish();
        } else {
            hideShipping();
            inflateCart();
        }
    }

    private void initViews() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        contentFrame = (FrameLayout) findViewById(R.id.content_frame);

    }

    private void upDateCart(CartProduct cartProduct) {
        if (fragmentCart != null)
            fragmentCart.updateCart(cartProduct);
    }

    private void inflateCart() {
        if (cartProduct.getProducts() != null ) {
            if(cartProduct.getProducts().size()==0)
                return;
            fragmentCart = FragmentCart.newInstance(cartProduct, cartEmpty);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragmentCart);
            transaction.commit();
            cartActivce = !cartActivce;
        }
    }

    private void hideCart() {
        if (fragmentCart != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentCart).commit();
        }
    }

    private void inflateShipping() {
        fragmentShipping = FragmentShipping.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragmentShipping).commit();
        cartActivce = !cartActivce;

    }

    private void hideShipping() {
        if (fragmentShipping != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentShipping).commit();
        }
    }

    private void setShipping(ShippingAddress shippingAddress) {
        prefs = new SharedPreferenceUtils(this);
        prefs.saveAddress(shippingAddress);
    }

  /*  private void setTimeout() {
        if (cartProduct.getExpiresOn() == null)
            return;
        long timeOut = Long.parseLong(cartProduct.getExpiresOn());
        prefs.setTimeOut(timeOut);
    }
*/
    private void notifyDeleteItem() {
        Snackbar bar = Snackbar.make(contentFrame, R.string.removing_from_cart, Snackbar.LENGTH_SHORT);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(this);
        contentLay.addView(item);
        bar.show();
    }


    @Override
    public void onProductLoaded(Product productObject) {

    }

    @Override
    public void onProductsLoaded(ArrayList<ProductDisplay> productDisplays) {

    }

    @Override
    public void onCartAdded(CartProduct product) {
        cartCount++;
        this.cartProduct = product;
        cartProduct.setTotalPrice();
        upDateCart(cartProduct);
        invalidateOptionsMenu();
        //setTimeout();
    }


    @Override
    public void onCartDelted(CartProduct product) {
        cartCount--;
        this.cartProduct = product;
        cartProduct.setTotalPrice();
        upDateCart(cartProduct);
        invalidateOptionsMenu();
      //  setTimeout();
    }

    @Override
    public void onCartLoaded(CartProduct product) {
        this.cartProduct = product;
        if (cartProduct != null && cartProduct.getProducts() != null) {
            cartProduct.setTotalPrice();
            cartCount = cartProduct.getProducts().size();
            cartEmpty = false;
            inflateCart();
            invalidateOptionsMenu();
         //   setTimeout();
        }
    }

    @Override
    public void onCartLoadedEmpty() {
        cartEmpty = true;
        hideCart();
        finish();
    }


    @Override
    public void onCheckOut() {

    }

    @Override
    public void onDeleteCartItem(Product product) {
        notifyDeleteItem();
        productApi.deleteProductCart(this, product);
    }

    @Override
    public void onAddCartItem(Product product) {

    }

    @Override
    public void onSetAddress() {
        hideCart();
        inflateShipping();
    }

    @Override
    public void onAddressSave(ShippingAddress shippingAddress) {
        setShipping(shippingAddress);
        hideShipping();
        inflateCart();
    }

}
