package com.flashsales;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.flashsales.Utils.Configs;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.Utils.Utils;
import com.flashsales.dao.ProductApi;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;
import com.flashsales.datamodel.User;
import com.flashsales.fragments.FragmentProducts;
import com.flashsales.fragments.FragmentTopSales;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity implements/* FragmentCart.OnCartEvent,*/
        View.OnClickListener
        , ProductApi.OnLoaded
        , NetworkChangeReciever.NetworkListener {
    private MyApplication mApplication;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private FrameLayout contentFrame;
    private FragmentProducts fragmentProducts;
    private FragmentTopSales fragmentTopSales;
    private String productName;
    private ArrayList<ProductDisplay> productList, filteredProducts;
    private boolean isCartOpen = false;
    private String searchPhrase = "";
    private MenuItem previousItem, firsItem;
    private ImageView ivAccount;
    private TextView tvAccount;
    private User user;
    private SharedPreferenceUtils prefs;
    int cartCount;
    private boolean faqClicked = false;
    private Toolbar toolbar;
    private ErrorAlert errorAlert;
    private boolean errorInflated = false;
    private boolean isProductsInflated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApplication = (MyApplication) getApplication();
        mApplication.setListenerNetwork(this);
        //cartList = mApplication.getCartList();
        if (mApplication.getDisplayArrayList() != null) {
            productList = mApplication.getDisplayArrayList();
        } else {
            reloadProducts();
        }
        initViews();
        inflateProducts();

        String topic = Configs.KEY_NOTI_CHANEL_NAME;
        if (BuildConfig.DEBUG) {
            topic = Configs.KEY_NOTI_CHANEL_NAME_DEBUG;
        }
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
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
        /*if(prefs.getTimeoutMills()>System.currentTimeMillis() && System.currentTimeMillis() != 0 ){
            prefs.setCartCount(0);
        }*/

        if (productList == null)
            reloadProducts();

        if (mApplication == null)
            mApplication = MyApplication.getInstance();
        mApplication.setListenerNetwork(this);
        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
        validateNavigation();
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!isCartOpen) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /// create notifcation here for  wining free product
       /* DBFavouriteProducts dbFavouriteProducts = new DBFavouriteProducts(this);
        DBViewedProducts dbViewedProducts = new DBViewedProducts(this);*/

        boolean recievedBonus = prefs.recievedFreePrizeOffer();
        if (prefs.getTimeoutMills() < System.currentTimeMillis() && !recievedBonus
                || cartCount == 0 && !recievedBonus) {
            setPrizeAlaram();
        }
    }

    private void checkAccount() {
        prefs = new SharedPreferenceUtils(this);
        user = prefs.getUser();

        if (!user.getImageFb().equals("") && ivAccount != null) {
            Picasso.with(this).load(user.getImageFb()).into(ivAccount);
        }
        if (!user.getName().equals("") && tvAccount != null) {
            Resources resources = getResources();
            String account = String.format(resources.getString(R.string.view_account), user.getName());
            tvAccount.setText(account/*user.getName() + " " + getResources().getString(R.string.account)*/);
        }
        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitle(R.string.flash_sales);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);
        headerView.setOnClickListener(this);
        ivAccount = headerView.findViewById(R.id.im_account);
        tvAccount = headerView.findViewById(R.id.tv_name);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (previousItem != null)
                    previousItem.setIcon(null);
                previousItem = item;

                item.setChecked(true);
                item.setIcon(R.drawable.ic_flash_on);


                int id = item.getItemId();
                String title = getResources().getString(R.string.flash_sales);
                switch (id) {
                    case R.id.nav_flashsales:
                        searchPhrase = Utils.SALE__DEFAULT;
                        title = getResources().getString(R.string.flash_sales);
                        break;
                    case R.id.nav_watches:
                        searchPhrase = Utils.SALE_WACTHES;
                        title = getResources().getString(R.string.nav_watches);
                        break;
                    case R.id.nav_earings:
                        searchPhrase = Utils.SALE_EARINGS;
                        title = getResources().getString(R.string.nav_earings);
                        break;
                    case R.id.nav_braclets:
                        searchPhrase = Utils.SALE_BRACELETS;
                        title = getResources().getString(R.string.nav_braclets);
                        break;
                    case R.id.nav_necklace:
                        searchPhrase = Utils.SALE_NECKLACE;
                        title = getResources().getString(R.string.nav_necklaces);
                        break;

                    case R.id.nav_sunglasses:
                        searchPhrase = Utils.SALE_SUNFLASSES;
                        title = getResources().getString(R.string.nav_sunglasses);
                        break;


                    default:
                        searchPhrase = Utils.SALE__DEFAULT;
                        break;
                }


                if (firsItem.isChecked() && !searchPhrase.equals(Utils.SALE__DEFAULT)) {
                    firsItem.setIcon(null);
                }

                toolbar.setTitle(title);
                updateList();
                mDrawerLayout.closeDrawers();
                return false;
            }
        });
        firsItem = mNavigationView.getMenu().getItem(0);
        firsItem.setIcon(R.drawable.ic_flash_on);
        firsItem.setChecked(true);

        contentFrame = (FrameLayout) findViewById(R.id.content_frame);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                toolbar,
                R.string.open,
                R.string.closed) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

        };
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        checkAccount();
    }


    private void inflateProducts() {
        if (isProductsInflated)
            return;
        if (productList != null && !isFinishing()) {
            fragmentProducts = FragmentProducts.newInstance(productList);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragmentProducts);
            transaction.commit();
            isProductsInflated = true;
        } else if (!isFinishing()) {
            Snackbar bar = Snackbar.make(contentFrame, R.string.loading_products, Snackbar.LENGTH_SHORT);
            ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
            ProgressBar item = new ProgressBar(this);
            contentLay.addView(item);
            bar.show();
        }
    }

    /*private void inflateTopSalesFragment(){
          FragmentTopSales fragmentTopSales = FragmentTopSales.newInstance();
          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          transaction.add(R.id.frame_top_sales,fragmentTopSales).commit();
    }*/

    private void loadCart() {
        Intent intent = new Intent(MainActivity.this, CartActivity.class);
        startActivity(intent);
    }



  /*  private void loadHistory() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
    }*/

    private void updateList(/*int amout*/) {
        if (productList == null)
            return;
        if (!searchPhrase.equals(Utils.SALE__DEFAULT)) {
            if (filteredProducts == null) {
                filteredProducts = new ArrayList<>();
            } else {
                filteredProducts.clear();
            }
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getName().toLowerCase().contains(searchPhrase.toLowerCase()) ||
                        productList.get(i).getShortName().toLowerCase().contains(searchPhrase.toLowerCase()) ||
                        productList.get(i).getBrand().toLowerCase().contains(searchPhrase.toLowerCase())) {
                    filteredProducts.add(productList.get(i));
                }
            }
            fragmentProducts.updateList(filteredProducts);
        } else if (searchPhrase.equals(Utils.SALE__DEFAULT)) {
            fragmentProducts.updateList(productList);
        }
    }

    private void reloadProducts() {
        productList = new ArrayList<>();
        ProductApi productApi = ProductApi.getInstance();
        productApi.okHttpRequestProducts(this);
        productApi.setListener(this);
    }

    private void intentHistoryAccount() {
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    private void validateNavigation() {
        MenuItem item = null;
        String title = getResources().getString(R.string.flash_sales);
        if (searchPhrase.contains(Utils.SALE__DEFAULT)) {
            item = mNavigationView.getMenu().findItem(R.id.nav_flashsales);
            title = getResources().getString(R.string.flash_sales);
        } else if (searchPhrase.contains(Utils.SALE_WACTHES)) {
            item = mNavigationView.getMenu().findItem(R.id.nav_watches);
            title = getResources().getString(R.string.nav_watches);
        } else if (searchPhrase.contains(Utils.SALE_EARINGS)) {
            item = mNavigationView.getMenu().findItem(R.id.nav_earings);
            title = getResources().getString(R.string.nav_earings);
        } else if (searchPhrase.contains(Utils.SALE_BRACELETS)) {
            item = mNavigationView.getMenu().findItem(R.id.nav_braclets);
            title = getResources().getString(R.string.nav_necklaces);
        } else if (searchPhrase.contains(Utils.SALE_NECKLACE)) {
            item = mNavigationView.getMenu().findItem(R.id.nav_necklace);
            title = getResources().getString(R.string.nav_necklaces);
        } else if (searchPhrase.contains(Utils.SALE_SUNFLASSES)) {
            item = mNavigationView.getMenu().findItem(R.id.nav_sunglasses);
            title = getResources().getString(R.string.nav_sunglasses);
        }
        if (previousItem != null) {
            previousItem.setIcon(null);
            previousItem.setChecked(false);
        }
        if (item != null) {
            item.setIcon(R.drawable.ic_flash_on);
            item.setChecked(true);
        }
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    private void setPrizeAlaram() {
        Intent intent = new Intent(MainActivity.this, PrizeBroadcastReciever.class);
        intent.putExtra("requestCode", 0);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3000, pendingIntent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_header:
                intentHistoryAccount();
                break;
            default:
                break;
        }
    }

    @Override
    public void onProductLoaded(Product productObject) {
    }

    @Override
    public void onProductsLoaded(ArrayList<ProductDisplay> productDisplays) {
        if (mApplication == null)
            mApplication = (MyApplication) getApplication();
        mApplication.setDisplayArrayList(productDisplays);
        if (productList == null)
            productList = new ArrayList<>();
        productList = productDisplays;
        inflateProducts();
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
        cartCount = 0;
        invalidateOptionsMenu();
    }

    @Override
    public void onCheckoutCartEmptied() {

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
