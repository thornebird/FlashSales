package com.flashsales;


import android.content.Intent;
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

import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.Utils.Utils;
import com.flashsales.dao.ProductApi;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;
import com.flashsales.datamodel.User;
import com.flashsales.fragments.FragmentProducts;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity implements/* FragmentCart.OnCartEvent,*/
        View.OnClickListener
        , ProductApi.OnLoaded {
    private MyApplication mApplication;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private FrameLayout contentFrame;

    private FragmentProducts fragmentProducts;
    private String productName;
    private ArrayList<ProductDisplay> productList, filteredProducts;
    // private CartProduct cartProduct;
    private boolean isCartOpen = false;
    private int searchAmout = 0;
    private MenuItem previousItem, firsItem;
    private ImageView ivAccount;
    private TextView tvAccount;
    private User user;
    private SharedPreferenceUtils prefs;
    int cartCount;
    private boolean faqClicked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApplication = (MyApplication) getApplication();
        //cartList = mApplication.getCartList();
        if(mApplication.getDisplayArrayList() != null) {
            productList = mApplication.getDisplayArrayList();

        }else{
            reloadProducts();
        }
        initViews();
        inflateProducts();
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

        if(productList == null)
            reloadProducts();

        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*if(prefs.getTimeoutMills()>System.currentTimeMillis() && System.currentTimeMillis() != 0 ){
            prefs.setCartCount(0);
        }*/
        if(productList == null)
            reloadProducts();

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
                /// inflate cart
                // inflateCart();
                loadCart();
                //inflateCart();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!isCartOpen) {
            super.onBackPressed();
        } else {
            //loadCart();
            //  closeCart();
        }
    }

    private void checkAccount() {
        prefs = new SharedPreferenceUtils(this);
        user = prefs.getUser();

        if (!user.getImageFb().equals("") && ivAccount != null) {
            Picasso.with(this).load(user.getImageFb()).into(ivAccount);
        }
        if (!user.getName().equals("") && tvAccount != null) {
            tvAccount.setText(user.getName() + " " + getResources().getString(R.string.account));
        }
        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
    }

    private void initViews() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);
        headerView.setOnClickListener(this);
        ivAccount = headerView.findViewById(R.id.im_account);
        tvAccount = headerView.findViewById(R.id.tv_name);


        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                item.setChecked(true);
                item.setIcon(R.drawable.ic_flash_on);

                if (previousItem != null)
                    previousItem.setIcon(null);
                previousItem = item;

                int id = item.getItemId();
                String title = "";
                switch (id) {
                    case R.id.nav_flashsales:
                        searchAmout = Utils.SALE__DEFAULT;
                        title = getResources().getString(R.string.flash_sales);
                        break;
                    case R.id.nav_under150:
                        searchAmout = Utils.SALE__150;
                        title = getResources().getString(R.string.under150);
                        toolbar.setTitle(title);
                        break;
                    case R.id.nav_under300:
                        searchAmout = Utils.SALE__300;
                        title = getResources().getString(R.string.under300);
                        break;
                    case R.id.nav_under500:
                        searchAmout = Utils.SALE_500;
                        title = getResources().getString(R.string.under500);
                        break;
                    case R.id.nav_under1000:
                        searchAmout = Utils.SALE_1000;
                        title = getResources().getString(R.string.under1000);
                        break;

                    case R.id.nav_under2500:
                        searchAmout = Utils.SALE_2500;
                        title = getResources().getString(R.string.under2500);
                        break;

                    case R.id.nav_faq:
                        faqClicked = !faqClicked;
                        loadFAQ();
                        break;
                    default:
                        searchAmout = Utils.SALE__DEFAULT;
                        break;
                }

                if (firsItem.isChecked() && searchAmout != 0 || faqClicked ) {
                    firsItem.setIcon(null);
                }
                toolbar.setTitle(title);
                updateList(searchAmout);
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
        if (productList != null) {
            fragmentProducts = FragmentProducts.newInstance(productList);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragmentProducts);
            transaction.commit();
        } else {
            Snackbar bar = Snackbar.make(contentFrame, R.string.loading_products, Snackbar.LENGTH_SHORT);
            ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
            ProgressBar item = new ProgressBar(this);
            contentLay.addView(item);
            bar.show();
        }
    }

    private void loadCart() {
        Intent intent = new Intent(MainActivity.this, CartActivity.class);
        startActivity(intent);
        // overridePendingTransition(R.anim.left_to_right,R.anim.left_to_right);
    }

    private void loadFAQ(){
        Intent intent = new Intent(MainActivity.this,FaqActivity.class);
        startActivity(intent);

    }


    private void updateList(int amout) {
        if (productList == null)
            return;
        if (amout != 0) {
            if (filteredProducts == null) {
                filteredProducts = new ArrayList<>();
            } else {
                filteredProducts.clear();
            }
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getPrice() <= amout) {
                    filteredProducts.add(productList.get(i));
                }
            }
            fragmentProducts.updateList(filteredProducts);
        } else if (amout == 0) {
            fragmentProducts.updateList(productList);
        }
    }

    private void reloadProducts() {
        productList = new ArrayList<>();
        ProductApi productApi = new ProductApi();
        productApi.okHttpRequestProducts();
        productApi.setListener(this);
    }

    private void intentLogin() {
        Intent intent = new Intent(MainActivity.this, AccountActivity.class);
        startActivity(intent);
    }

    /*private void updateCart(CartProduct cartProduct) {
        cartCount = cartProduct.getProducts().size();
        invalidateOptionsMenu();
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_header:
                intentLogin();
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
}
