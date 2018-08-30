package com.flashsales;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.adapters.AdapterRecyclerView;
import com.flashsales.datamodel.OrderObject;
import com.flashsales.datamodel.PrizeOrderObject;
import com.flashsales.datamodel.Product;
import com.squareup.picasso.Picasso;

public class OrderInformationActivity extends AppCompatActivity implements NetworkChangeReciever.NetworkListener {

    public final static String KEY_ORDER = "keyOrder";
    private OrderObject order;
    private PrizeOrderObject prizeOrderObject;
    private int cartCount = 0;
    private SharedPreferenceUtils prefs;
    private MyApplication myApplication;
    private ErrorAlert errorAlert;
    private boolean errorInflated = false;
    private boolean isFree = false;
    private  ConstraintLayout layoutParent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);

        if (getIntent().getExtras() != null) {
            Object incomingObject = getIntent().getExtras().getParcelable(KEY_ORDER);
            if (incomingObject instanceof OrderObject) {
                order = (OrderObject) incomingObject;
            } else if (incomingObject instanceof PrizeOrderObject) {
                prizeOrderObject = (PrizeOrderObject) incomingObject;
                isFree = true;
            }
        }

        prefs = new SharedPreferenceUtils(this);
        cartCount = prefs.getCartCount();
        myApplication = MyApplication.getInstance();
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myApplication == null)
            myApplication = MyApplication.getInstance();
        myApplication.setListenerNetwork(this);
        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ScrollView sv = (ScrollView)findViewById(R.id.sv);
        sv.fullScroll(View.FOCUS_UP);

        if (!isFree) {
            getSupportActionBar().setTitle(getString(R.string.order_summary) + " " + order.getUser().getEmail());

        } else {
            getSupportActionBar().setTitle(getString(R.string.order_summary) + " " + prizeOrderObject.getUser().getEmail());
        }
        layoutParent = (ConstraintLayout) findViewById(R.id.parent);
        TextView tvDate = (TextView) findViewById(R.id.tv_date);
        TextView tvAmountPaid = (TextView) findViewById(R.id.tv_amount_paid);
        TextView tvShippingAddress = (TextView) findViewById(R.id.tv_shipping_address);
        TextView tvItemsOrdered = (TextView) findViewById(R.id.tv_products_ordered);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setFocusable(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);

        if (!isFree) {

            AdapterRecyclerView adapterRecyclerView = new AdapterRecyclerView(this,
                    R.layout.item_product_orderinformation, order.getCartProduct().getProducts(), new AdapterRecyclerView.ClickListener() {
                @Override
                public void onItemAdapterClick(Object object, int position, View view) {

                }

                @Override
                public void onCartItemDelted(Product product, int layoutPosition) {

                }

                @Override
                public void onCartItemAdded(Product product) {

                }

                @Override
                public void onFavoruriteProductDelete(Product product) {

                }
            });

            rv.setNestedScrollingEnabled(false);
            rv.setAdapter(adapterRecyclerView);
            tvDate.setText(formattedDate(order.getPaymentObject().getPaypalResponse().getCreatedTime()));
            tvAmountPaid.setText(getString(R.string.currency) + " " + order.getCartProduct().getSalePrice() + "");
            tvShippingAddress.setText(order.getShippingAddress().toString());

        } else if (isFree) {
            layoutParent.removeView(rv);

            tvShippingAddress.setText(prizeOrderObject.getAddress().toString());
            tvDate.setText(formattedDate(prizeOrderObject.getPaymentObject().getPaypalResponse().getCreatedTime()));
            tvAmountPaid.setText(getString(R.string.currency) + " " + prizeOrderObject.getFreeProduct().getShippingPrice());

            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.item_product_orderinformation, null,false);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ImageView imProduct = (ImageView) view.findViewById(R.id.im_product);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            TextView tvBrand = (TextView) view.findViewById(R.id.tv_brand);
            TextView tvPrice = (TextView) view.findViewById(R.id.tv_price);
            TextView tvCount = (TextView) view.findViewById(R.id.tv_count);

            if (prizeOrderObject != null) {
                Picasso.with(this).load(prizeOrderObject.getFreeProduct().getImages().get(0)).placeholder(R.drawable.logo).into(imProduct);
                tvName.setText(prizeOrderObject.getFreeProduct().getName());
                tvBrand.setText(prizeOrderObject.getFreeProduct().getBrand());
                tvPrice.setText(getString(R.string.currency)+" "+prizeOrderObject.getFreeProduct().getShippingPrice());
                tvCount.setVisibility(View.INVISIBLE);
            }

            view.setId(View.generateViewId());
            layoutParent.addView(view);


            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(layoutParent);
            constraintSet.connect(view.getId(), ConstraintSet.TOP, tvItemsOrdered.getId(), ConstraintSet.BOTTOM);
            constraintSet.applyTo(layoutParent);

  /*          ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) layoutParent.getLayoutParams();
            lp.height = 0;
            lp.width = 0;
            layoutParent.setLayoutParams(lp);*/


        }


    }

    private String formattedDate(String date) {
        return date.replace("T", " ").replace("Z", " ");
    }

    private void loadCart() {
        Intent intent = new Intent(OrderInformationActivity.this, CartActivity.class);
        startActivity(intent);
        finish();
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

    @Override
    public void networkAvailable() {
        hideInternetError();
    }

    @Override
    public void networkUnavailable() {
        showInternetError();

    }
}
