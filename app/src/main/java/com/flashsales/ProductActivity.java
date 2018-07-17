package com.flashsales;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.ArrayList;


import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.Utils.Utils;
import com.flashsales.dao.ProductApi;
import com.flashsales.dao.DBViewedProducts;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.Product;
import com.flashsales.adapters.AdapterFragmentPager;
import com.flashsales.datamodel.ProductDisplay;
import com.flashsales.datamodel.Review;
import com.flashsales.fragments.FragmentExpandSwipe;
import com.flashsales.fragments.FragmentItemAdded;

import com.flashsales.fragments.FragmentBuy;
import com.flashsales.fragments.FragmentProductProfile;
import com.flashsales.fragments.FragmentVarientSelection;
import com.flashsales.fragments.FragmentYoutube;


public class ProductActivity extends AppCompatActivity implements FragmentBuy.OnBuyEvent,
        FragmentProductProfile.OnProductEvent,
        FragmentYoutube.OnVideoEvent,
        /*  FragmentCart.OnCartEvent,*/
        FragmentVarientSelection.OnSelectionEvent,
        FragmentExpandSwipe.OnSwipeEvent,
        ProductApi.OnLoaded {

    private MyApplication myApplication;
    public final static String KEY_PRODUCT = "product";
    private final static String KEY_ISVIDEO = "isVideoOpen";
    private ProductDisplay productDisplay;
    private Product activeProduct;
    private FrameLayout frameBuy, frameLayoutBottom, frameLayout, frameCart;
    private TextView tvViewing;
    private AdapterFragmentPager adapterFragmentPager;
    private FragmentBuy fragmentBuy;
    private FragmentYoutube fragmentYoutube;
    private FragmentItemAdded fragmentItemAdded;
    private FragmentVarientSelection fragmentVarientSelection;
    private FragmentExpandSwipe fragmentExpandSwipe;
    private boolean isVideoOpen = false;
    private int currentVariant = 0;
    private Handler handler;
    private Handler handlerTimer;
    private Runnable runnable;
    private Runnable runnableViewing;
    private boolean isCart = false;
    private ViewPager vp;
    private ArrayList<Review> reviewArrayList;
    private ProgressAlert progressAlert;
    private SharedPreferenceUtils prefs;
    private int cartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        initViews();

        productDisplay = getIntent().getExtras().getParcelable(KEY_PRODUCT);
        myApplication = (MyApplication) getApplicationContext();
        myApplication.reorderReviews();
        reviewArrayList = myApplication.getReviews();

        prefs = new SharedPreferenceUtils(this);
        cartCount = prefs.getCartCount();

        progressAlert = new ProgressAlert(this, getResources().getString(R.string.loading),
                productDisplay.getName());
        ProductApi productApi = ProductApi.getInstance();
        productApi.setListener(this);
        productApi.okhttpRequestProduct(productDisplay.getBrand(), productDisplay.getName());
    }
    //initViews();


    @Override
    protected void onResume() {
        super.onResume();
     /*   if (prefs != null) {
            if (prefs.getTimeoutMills() > System.currentTimeMillis() && prefs.getTimeoutMills() != 0)
                prefs.setCartCount(0);
        }*/
      /*  if(prefs.getTimeoutMills()>System.currentTimeMillis() && System.currentTimeMillis() != 0 ){
            prefs.setCartCount(0);
        }*/
        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
    }

    @Override
    protected void onStart() {
        super.onStart();
    /*    if (prefs != null) {
            if (prefs.getTimeoutMills() > System.currentTimeMillis() && prefs.getTimeoutMills() != 0)
                prefs.setCartCount(0);
        }*/
   /*     if(prefs.getTimeoutMills()>System.currentTimeMillis() && System.currentTimeMillis() != 0 ){
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
            case R.id.mi_cart:
                loadCart();
                break;
            case android.R.id.home:
                /*onBackPressed();*/
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ISVIDEO, isVideoOpen);
        if (handler != null)
            handler.removeCallbacks(runnable);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isVideoOpen = savedInstanceState.getBoolean(KEY_ISVIDEO);
        if (fragmentYoutube != null)
            fragmentYoutube.setListener(this);
    }

    @Override
    public void onBackPressed() {
        if (!isVideoOpen && !isCart) {
            super.onBackPressed();
        }
    }


    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        vp = (ViewPager) findViewById(R.id.vp);
       /* adapterFragmentPager = new AdapterFragmentPager(activeProduct, getSupportFragmentManager(), reviewArrayList);
        vp.setAdapter(adapterFragmentPager);
        vp.setOffscreenPageLimit(2);*/

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp);

        frameBuy = (FrameLayout) findViewById(R.id.frame_layout_pops);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayoutBottom = (FrameLayout) findViewById(R.id.frame_layout_bottom);
        frameCart = (FrameLayout) findViewById(R.id.frame_cart);

    }

    private void loadCart() {
        Intent intent = new Intent(ProductActivity.this, CartActivity.class);
        startActivity(intent);
    }

    private void fragmentBuy() {
        if (fragmentBuy == null) {
            fragmentBuy = FragmentBuy.newInstance(activeProduct.getPrice(), activeProduct.getRetailPrice());
        }
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_up);
        transaction.add(R.id.frame_layout_pops, fragmentBuy, "fragmentBuy").commit();

    }

    private void hideBuyFragment() {
        if (fragmentBuy != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentBuy).commit();
        }
    }

    private void inflateVideoDialog() {
        if (fragmentYoutube == null) {
            fragmentYoutube = FragmentYoutube.newInstance();
        }
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_layout, fragmentYoutube, "fragmentBuy").commit();
        isVideoOpen = !isVideoOpen;
    }

    private void hideVideoFragment() {
        if (fragmentYoutube != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentYoutube).commit();
            isVideoOpen = !isVideoOpen;
        }
    }

    private void inflateVariants() {
        if (fragmentVarientSelection == null) {
            fragmentVarientSelection = FragmentVarientSelection.newInstance(activeProduct);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_upv2,
                R.animator.slide_downv2,
                R.animator.slide_upv2,
                R.animator.slide_downv2);
        transaction.add(R.id.frame_layout_bottom, fragmentVarientSelection).commit();
    }

    private void hideVarients() {
        if (fragmentVarientSelection != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(0,R.anim.slide_down);
            transaction.setCustomAnimations(R.animator.slide_upv2,
                    R.animator.slide_downv2,
                    R.animator.slide_upv2,
                    R.animator.slide_downv2);
            transaction.remove(fragmentVarientSelection).commit();
        }
    }

    private void inflateImageFragment() {
        if (fragmentExpandSwipe == null) {
            fragmentExpandSwipe = FragmentExpandSwipe.newInstance(activeProduct);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_layout, fragmentExpandSwipe).commit();
    }

    private void hideImageFragment() {
        if (fragmentExpandSwipe != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentExpandSwipe).commit();
        }
    }


    private void updateAlaram(long mills) {
       // long tenMinsLess = mills - 3540000;
        long tenMinsLess = mills - 900000;
        Intent intent = new Intent(ProductActivity.this, CartBroadCastReciever.class);
        intent.putExtra("requestCode", Utils.reqCodeCart);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Utils.reqCodeCart, intent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC_WAKEUP, tenMinsLess, pendingIntent);
    }



    private void setTimeoutAlarm(long timeout) {

        Intent intent = new Intent(ProductActivity.this, TimeBroadcastReciever.class);
        intent.putExtra("timeoutKey", Utils.reqCodeTimeout);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, Utils.reqCodeTimeout, intent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent1);
    }



    private void notifyAddedCart() {
        Snackbar bar = Snackbar.make(frameLayout, R.string.adding_to_cart, Snackbar.LENGTH_SHORT);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(this);
        contentLay.addView(item);
        bar.show();
    }

    @Override
    public void onBuy() {
        inflateVariants();
    }

    @Override
    public void onExpandImage() {
        inflateImageFragment();
    }

    @Override
    public void onVideoClosed() {
        hideVideoFragment();
    }


    @Override
    public void onVarientsClose() {
        hideVarients();
    }

    @Override
    public void onAddToCart(int count, String valueSelected) {
        notifyAddedCart();
        String value = !valueSelected.equals("") ? valueSelected : "default";
        activeProduct.setStock(count);
        activeProduct.setVarientSelected(value);
        ProductApi productApi = ProductApi.getInstance();
        productApi.addProductCart(this, activeProduct);

    }

    @Override
    public void onSwipeClosed() {
        hideImageFragment();
    }

    @Override
    public void onProductLoaded(Product productObject) {
        activeProduct = productObject;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //initViews();

                adapterFragmentPager = new AdapterFragmentPager(activeProduct, getSupportFragmentManager(), reviewArrayList);
                vp.setAdapter(adapterFragmentPager);
                vp.setOffscreenPageLimit(2);
                adapterFragmentPager.notifyDataSetChanged();

                fragmentBuy();
                progressAlert.stopAlert();
            }
        });
        DBViewedProducts viewedProducts = new DBViewedProducts(this);
        if (!viewedProducts.ifExists(activeProduct)) {
            activeProduct.setImage(activeProduct.getImagePaths().get(0));
            viewedProducts.addProduct(activeProduct);
        }
    }

    @Override
    public void onProductsLoaded(ArrayList<ProductDisplay> productDisplays) {

    }

    @Override
    public void onCartAdded(CartProduct cartProduct) {
        cartCount = cartProduct.getProducts().size();
        invalidateOptionsMenu();
        long time = Long.parseLong(cartProduct.getExpiresOn());
        updateAlaram(time);
        long mills  = Long.parseLong(cartProduct.getExpiresOn());
      //  setTimeoutAlarm(mills);
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


}
