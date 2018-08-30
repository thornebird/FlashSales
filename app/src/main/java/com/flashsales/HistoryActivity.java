package com.flashsales;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.Utils.Utils;
import com.flashsales.adapters.AdapterFragmentPager;

import com.flashsales.dao.DBFavouriteProducts;
import com.flashsales.dao.DBViewedProducts;
import com.flashsales.dao.FreeOrderDao;
import com.flashsales.dao.OrderDao;
import com.flashsales.datamodel.OrderObject;
import com.flashsales.datamodel.PrizeOrderObject;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;
import com.flashsales.datamodel.User;
import com.flashsales.fragments.FragmentAccountEdit;
import com.flashsales.fragments.FragmentOrderHistory;
import com.flashsales.fragments.FragmentSavedProducts.OnViwedProductEvent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements OnViwedProductEvent
,FragmentAccountEdit.OnAccountEditSaved
,NetworkChangeReciever.NetworkListener
,FragmentOrderHistory.OnOrderClicked{
    private ArrayList<Product> viewedProducts, favouriteProducts;
    private HashMap<String, ArrayList<Product>> mapProducts;
    private ArrayList<OrderObject> orders;
    private ArrayList<PrizeOrderObject> prizeOrders;
    private ViewPager vp;
    private TabLayout tabLayout;
    private AdapterFragmentPager adapterFragmentPager;
    private SharedPreferenceUtils prefs;
    private int cartCount;
    private ProductsLoader loader;
    private final static int STATE_VIEWIED = 0;
    private final static int STATE_FAVOURITES= 1;
    private int currentState = 0;
    private boolean isChanged = false;
    private ConstraintLayout  frameLayout;
    private boolean isloading = true;
    private User user;
    private MyApplication myApplication;
    private ErrorAlert errorAlert;
    private boolean errorInflated = false;
    public final static String KEY_SCROLL_POS = "keyScrollPosition";
    public final static int  POS_ORDER_HISTORY  = 2;
    private int scrollPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null &&bundle.containsKey(KEY_SCROLL_POS)){
            scrollPosition = bundle.getInt(KEY_SCROLL_POS);
        }
        myApplication = MyApplication.getInstance();
        myApplication.setListenerNetwork(this);
        prefs = new SharedPreferenceUtils(this);
        user = prefs.getUser();
        cartCount = prefs.getCartCount();
        orders = new ArrayList<>();
        prizeOrders = new ArrayList<>();

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
            case R.id.mi_cart:
                loadCart();
                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        if(myApplication!=null)
            myApplication.setListenerNetwork(this);
        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
    }

/*
    @Override
    protected void onStart() {
        super.onStart();
        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
    }*/

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Resources resources = getResources();

        Resources res = getResources();
        String name = user.getName();
        @SuppressLint("StringFormatInvalid") String title = String.format(res.getString(R.string.account_information_for),name);
        getSupportActionBar().setTitle(title);

        frameLayout = (ConstraintLayout)findViewById(R.id.frame_layout);
        vp = (ViewPager) findViewById(R.id.vp_products);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(!isChanged)
                    isChanged= !isChanged;
              currentState = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp);

        loader = new ProductsLoader();
        loader.execute();

    }

    private void getProductsList() {
        Iterator it = mapProducts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            ArrayList<Product> products = (ArrayList) entry.getValue();
            if (key.equals(Utils.keyFavourieProducts)) {
                if (favouriteProducts == null)
                    favouriteProducts = new ArrayList<>();
                if (favouriteProducts.size() > 0)
                    favouriteProducts.clear();
                favouriteProducts.addAll(products);

            } else if (key.equals(Utils.keyViewedProducts)) {
                if (viewedProducts == null)
                    viewedProducts = new ArrayList<>();
                if (viewedProducts.size() > 0)
                    viewedProducts.clear();
                viewedProducts.addAll(products);
            }
        }
           setVp();

            OrderDao orderDao = new OrderDao();
            orderDao.updateOrderHistory(new OrderDao.OrdersListener() {
                @Override
                public void OnOrderLoaded(ArrayList<OrderObject> orderObjects) {
                    orders = orderObjects;

                }
            },false,user.getEmail(),"");
            loadFreeOrders();
//load free products for list
    }

    private void loadFreeOrders(){
        FreeOrderDao freeOrderDao = new FreeOrderDao();
        freeOrderDao.updateFreeOrders(new FreeOrderDao.FreeOrdersListener() {
            @Override
            public void onFreeOrdersLoaded(ArrayList<PrizeOrderObject> freeOrders) {
                prizeOrders = freeOrders;
                setVp();
            }
        },false,user.getEmail(),"");
    }

    private void setVp() {
        adapterFragmentPager = new AdapterFragmentPager(this,
                getSupportFragmentManager(),
                AdapterFragmentPager.STATE_HISTORY_ACTIVITY,
                viewedProducts,
                favouriteProducts,orders,prizeOrders,user);

        vp.setAdapter(adapterFragmentPager);
        vp.setOffscreenPageLimit(3);
        adapterFragmentPager.notifyDataSetChanged();
        if(isChanged && vp!= null)
            vp.setCurrentItem(currentState);
        if(scrollPosition!=0) {
            vp.setCurrentItem(scrollPosition);
        }
    }

    private void intentViewProduct(Product product) {
        ProductDisplay productDisplay = new ProductDisplay();
        productDisplay.setName(product.getName());
        productDisplay.setBrand(product.getBrand());
        productDisplay.setRetailPrice(Double.parseDouble(product.getRetailPrice()));
        productDisplay.setPrice(Double.parseDouble(product.getPrice()));
        productDisplay.setShortName(product.getShortName());
        productDisplay.setFakeRating(product.getFakeRating());

        Intent intent = new Intent(HistoryActivity.this, ProductActivity.class);
        intent.putExtra(ProductActivity.KEY_PRODUCT, productDisplay);
        startActivity(intent);


    }

    private void deleteItem(Product product){
        String message = "";
        if(vp.getCurrentItem()==STATE_VIEWIED){
            DBViewedProducts db = new DBViewedProducts(this);
            if(db.deleteProduct(product)){
                loader = new ProductsLoader();
                loader.execute();
                message = getString(R.string.deleted_product_favs);
                // delete success
            }else{
                // delete failed
                message = "";
            }
        }else if(vp.getCurrentItem() == STATE_FAVOURITES) {
            DBFavouriteProducts db = new DBFavouriteProducts(this);
            if (db.deleteProduct(product)) {
                loader = new ProductsLoader();
                loader.execute();
                message = getString(R.string.deleted_product_viewed);
                // delete success
            } else {
                message = getString(R.string.error_product_viewed);
                // delete failed
            }
        }
        if(!message.equals(""))
            notifyUser(message);
    }

    private void dialogDeleteProduct(final Product product){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);

        View view  = getLayoutInflater().inflate(R.layout.dialog_delete_product,null);
        dialog.setContentView(view);

        ImageView ivProduct = (ImageView)view.findViewById(R.id.iv_product);
        Picasso.with(this).load(product.getImage()).placeholder(R.drawable.logo).into(ivProduct);

        ImageView ivCancel =(ImageView)view.findViewById(R.id.iv_delete);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnRemove = (Button)view.findViewById(R.id.btn_remove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(product);
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void loadCart() {
        Intent intent = new Intent(HistoryActivity.this, CartActivity.class);
        startActivity(intent);
    }

    @Override
    public void onViewClicked(Product product) {
        if (product != null)
            intentViewProduct(product);
    }

    @Override
    public void onDeleteFavouriteProduct(Product productNew) {
       // deleteItem(productNew);
        dialogDeleteProduct(productNew);
    }

    @Override
    public void onCartEmptied() {
        vp.destroyDrawingCache();
        vp.setCurrentItem(currentState);
    }

    private void notifyUser(String message) {
        Snackbar bar = Snackbar.make(frameLayout,message, Snackbar.LENGTH_SHORT);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(this);
        contentLay.addView(item);
        bar.show();
    }

    @Override
    public void onSaveAccount(User user) {
        this.user = user;
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
        if(!errorInflated) {
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

    @Override
    public void OnOrderClicked(OrderObject orderObject) {
        Intent intent = new Intent(HistoryActivity.this, OrderInformationActivity.class);
        intent.putExtra(OrderInformationActivity.KEY_ORDER,orderObject);
        startActivity(intent);
    }

    @Override
    public void OnFreeOrderClicked(PrizeOrderObject prizeOrderObject) {
        Intent intent = new Intent(HistoryActivity.this,OrderInformationActivity.class);
        intent.putExtra(OrderInformationActivity.KEY_ORDER,prizeOrderObject);
        startActivity(intent);
    }

    private class ProductsLoader extends AsyncTask<Void, Void, HashMap<String, ArrayList<Product>>> {


        private ProgressAlert progressAlert;
        private DBViewedProducts dbViewedProducts = new DBViewedProducts(HistoryActivity.this);
        private DBFavouriteProducts dbFavouriteProducts = new DBFavouriteProducts(HistoryActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(isloading) {
                progressAlert = new ProgressAlert(HistoryActivity.this, "loadin viewd products", "message");
            }
        }


        @Override
        protected HashMap<String, ArrayList<Product>> doInBackground(Void... voids) {

            HashMap<String, ArrayList<Product>> map = new HashMap<>();
            ArrayList<Product> productsViewd = dbViewedProducts.getViewedProducts();
            ArrayList<Product> productsFavouites = dbFavouriteProducts.getFavouriteProducts();

            map.put(Utils.keyViewedProducts, productsViewd);
            map.put(Utils.keyFavourieProducts, productsFavouites);

            return map;
        }

        @Override
        protected void onPostExecute(HashMap<String, ArrayList<Product>> aVoid) {
            super.onPostExecute(aVoid);
            if(isloading) {
                progressAlert.stopAlert();
                isloading =  !isloading;
            }
            mapProducts = aVoid;
            getProductsList();
        }
    }
}
