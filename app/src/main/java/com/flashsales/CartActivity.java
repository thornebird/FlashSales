package com.flashsales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.flashsales.Utils.AnswersEvents;
import com.flashsales.Utils.Configs;
import com.flashsales.Utils.FBEvents;
import com.flashsales.Utils.FirebaseEvents;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.dao.OrderDao;
import com.flashsales.dao.ProductApi;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.OrderObject;
import com.flashsales.datamodel.PaymentObject;
import com.flashsales.datamodel.PaypalClient;
import com.flashsales.datamodel.PaypalResponse;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;
import com.flashsales.datamodel.ShippingAddress;
import com.flashsales.fragments.FragmentCart;
import com.flashsales.fragments.FragmentEmptyCart;
import com.flashsales.fragments.FragmentPaymentConfirmation;
import com.flashsales.fragments.FragmentShipping;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

public class CartActivity extends AppCompatActivity implements ProductApi.OnLoaded,
        FragmentCart.OnCartEvent,
        FragmentShipping.OnAddressEvent
        , NetworkChangeReciever.NetworkListener {


    private FragmentCart fragmentCart;
    private FragmentEmptyCart fragmentEmptyCart;
    private FragmentShipping fragmentShipping;
    private ProductApi productApi;
    private CartProduct cartProduct;
    private boolean cartActivce = false;
    private boolean cartEmpty;
    private boolean paymentSuccess = false;
    private int cartCount;
    private SharedPreferenceUtils prefs;
    private FrameLayout contentFrame;
    private ProgressAlert alert;
    private MyApplication mApplication;
    private ErrorAlert errorAlert;
    private boolean errorInflated = false;
    private MenuItem menuItem;
    private int discountPercent = 0;
    private boolean discountOpened = false;
    public final static String KEY_FREE_PRIZE = "keyFree";
    public final static String KEY_FREE_PRODUCT = "keyProduct";
    private boolean isFreePrize=false;
    private boolean isFreeShipping = true;
    private Calendar calendar;
    private Product product;
    private  long mills = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        if(getIntent().getExtras()!=null){
            isFreePrize = getIntent().getExtras().getBoolean(KEY_FREE_PRIZE);
            if(isFreePrize)
                isFreeShipping =true;

            product = getIntent().getExtras().getParcelable(KEY_FREE_PRODUCT);
        }
        mApplication = (MyApplication) getApplication();
        mApplication.setListenerNetwork(this);
        initViews();

        if(!isFreePrize) {
            alert = new ProgressAlert(this, getString(R.string.loading), getString(R.string.loading_cart_products));
            prefs = new SharedPreferenceUtils(this);
            cartCount = prefs.getCartCount();
            productApi = ProductApi.getInstance();
            productApi.setListener(this);
            productApi.viewCart(this);
        }else {
            inflateCart();
        }
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
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

        if (mApplication == null)
            mApplication = MyApplication.getInstance();
        mApplication.setListenerNetwork(this);
        cartCount = prefs.getCartCount();
        invalidateOptionsMenu();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menuItem = menu.findItem(R.id.mi_cart);
        menuItem.setIcon(ConverterCartBadge.convertLayoutToImage(this, cartCount, R.drawable.ic_shopping_cart));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (cartActivce || cartEmpty || paymentSuccess) {
                    if (!discountOpened && !cartEmpty) {
                        showDiscountPop();
                    } else {
                        this.finish();
                    }
                } else {
                    onBackPressed();
                }


                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (cartActivce || cartEmpty || paymentSuccess) {
            if (/*prefs.isFirstTime() &&*/ !discountOpened && !cartEmpty) {
                showDiscountPop();
            } else {
                this.finish();
            }
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
        getSupportActionBar().setTitle(getString(R.string.cart_flashsales));

        contentFrame = (FrameLayout) findViewById(R.id.content_frame);

    }

    private void upDateCart(CartProduct cartProduct) {
        if (fragmentCart != null)
            fragmentCart.updateCart(cartProduct);
    }

    private void inflateCart() {
        if (cartProduct.getProducts() != null && !isFinishing() ||isFreePrize &&!isFinishing()) {
            if (cartProduct.getProducts().size() == 0)
                return;


            if(isFreePrize){
                if(calendar == null) {
                    calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE,3);
                    mills = calendar.getTimeInMillis() - System.currentTimeMillis();
                }
            }

            fragmentCart = FragmentCart.newInstance(cartProduct, cartEmpty,isFreePrize,mills);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragmentCart);
            transaction.commit();
            cartActivce = !cartActivce;
        }
    }

    private void hideCart() {
        if (fragmentCart != null && !isFinishing()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentCart).commit();
        }
    }

    private void inflateShipping() {
        if (!isFinishing()) {
            fragmentShipping = FragmentShipping.newInstance(0.0);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragmentShipping).commit();
            cartActivce = !cartActivce;
        }

    }

    private void hideShipping() {
        if (fragmentShipping != null && !isFinishing()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentShipping).commit();
        }
    }

    private void setShipping(ShippingAddress shippingAddress) {
        prefs = new SharedPreferenceUtils(this);
        prefs.saveAddress(shippingAddress);
    }


    private void inflateEmptyFragment() {
        if (!isFinishing()) {
            fragmentEmptyCart = FragmentEmptyCart.newInstance(getString(R.string.your_cart_is_empty));
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragmentEmptyCart).commit();
        }
    }

/*
    private void hideEmptyCart() {
        if (fragmentEmptyCart != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentEmptyCart).commit();
        }
    }
*/

    private void inflatePaymentConfirmation() {
        if (!isFinishing()) {
            FragmentPaymentConfirmation fragmentPaymentConfirmation = FragmentPaymentConfirmation.newInstance(cartProduct.getSalePrice() + "");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.content_frame, fragmentPaymentConfirmation).commit();
        }
    }


    private void notifyDeleteItem() {
        Snackbar bar = Snackbar.make(contentFrame, R.string.removing_from_cart, Snackbar.LENGTH_SHORT);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(this);
        contentLay.addView(item);
        bar.show();
    }

    private void notifyUser(String message) {
        Snackbar.make(contentFrame, message, Snackbar.LENGTH_SHORT).show();
    }


    private void payment(String amount) {
        PayPalConfiguration config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                .clientId(Configs.PAYPAL_CLIENT_ID_RELEASE);

        Intent serviceConfig = new Intent(this, PayPalService.class);
        serviceConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(serviceConfig);

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(amount), "USD", "Shopping cart items", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent paymentConfig = new Intent(this, PaymentActivity.class);
        paymentConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        paymentConfig.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(paymentConfig, 0);

    }

    private void dialogDeleteProduct(final Product product) {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.dialog_delete_product, null);
        dialog.setContentView(view);

        ImageView ivProduct = (ImageView) view.findViewById(R.id.iv_product);
        Picasso.with(this).load(product.getImage()).placeholder(R.drawable.logo).into(ivProduct);

        ImageView ivCancel = (ImageView) view.findViewById(R.id.iv_delete);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnRemove = (Button) view.findViewById(R.id.btn_remove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDeleteItem();
                productApi.deleteProductCart(getBaseContext(), product, false);
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

    private void showDiscountPop() {

        final Dialog dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_discount, null);
        dialog.setContentView(view);
        dialog.setCancelable(false);

        Button btnClaimDiscount = (Button) view.findViewById(R.id.btn_discount);
        btnClaimDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discountPercent = 10;
                // String amount = discountPercent+"";
                dialog.dismiss();
                Resources resources = getResources();
                String text = String.format(resources.getString(R.string.discount_added), prefs.getUser().getName(), discountPercent);
                Snackbar bar = Snackbar.make(contentFrame, text, Snackbar.LENGTH_SHORT);
                final double discount = discountPercent;

                double amountToRemove = cartProduct.getSalePrice() * (discount / 100);
                final double total = cartProduct.getSalePrice() - amountToRemove;
                cartProduct.setSalePrice(total);
                bar.addCallback(new Snackbar.Callback() {

                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        //see Snackbar.Callback docs for event details
                        fragmentCart.updateSalesPrices(total, discountPercent + "");

                    }

                    @Override
                    public void onShown(Snackbar snackbar) {

                    }
                });
                bar.show();


            }
        });

        ImageView ivClose = (ImageView) view.findViewById(R.id.iv_delete);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //finish();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        discountOpened = true;
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(
                    PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    notifyUser(getString(R.string.payment_succesfull));
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    final Gson gson = gsonBuilder.create();
                    PaymentObject paymentObject = gson.fromJson(confirm.toJSONObject().toString(4), PaymentObject.class);

                    /*  JSONPObject paymentObjectJson  = new JSONPObject(paymentObject);
                    PaypalResponse paypalResponse = gson.fromJson(confirm.toJSONObject().toString(4),PaypalResponse.class);
                    PaypalClient paypalClient = gson.fromJson(confirm.toJSONObject().toString(4),PaypalClient.class);*/

                    JSONObject jsonObjectResult = confirm.toJSONObject();
                    JSONObject jsonClient = jsonObjectResult.getJSONObject("client");
                    JSONObject jsonResponse = jsonObjectResult.getJSONObject("response");

                    PaypalResponse paypalResponse = gson.fromJson(jsonResponse.toString(4), PaypalResponse.class);
                    PaypalClient paypalClient = gson.fromJson(jsonClient.toString(4), PaypalClient.class);
                    //Thank you for payment page needed
                    paymentObject.setClient(paypalClient);
                    paymentObject.setPaypalResponse(paypalResponse);
                    Log.i("sampleapp", confirm.toJSONObject().toString(4));

                    /////////
                    OrderObject orderObject = new OrderObject();
                    orderObject.setCartProduct(cartProduct);
                    orderObject.setPaymentObject(paymentObject);
                    orderObject.setUser(prefs.getUser());
                    orderObject.setShippingAddress(prefs.getAddress());
                    orderObject.setEmailIdentifier(prefs.getUser().getEmail());
                    OrderDao orderDao = new OrderDao();
                    orderDao.uploadOrder(this, orderObject);
                    FBEvents fbEvents = new FBEvents(getApplication());
                    // Integer integer = new Integer(cartProduct.getSalePrice());
                    /*ouble price = integer.doubleValue();*/
                    fbEvents.logPurchase(getString(R.string.currency), orderObject.getEmailIdentifier(), paymentObject.getPaypalResponse().getId(),
                            cartProduct.getSalePrice());


                    AnswersEvents answersEvents = new AnswersEvents();
                    answersEvents.purchaseEvent(cartProduct.getSalePrice(),
                            orderObject.getPaymentObject().getPaypalResponse().getId(), orderObject.getCartProduct().getProducts().size());

                    FirebaseEvents firebaseEvents = new FirebaseEvents(this);
                    firebaseEvents.purchaseEvent(orderObject.getPaymentObject().getPaypalResponse().getId(),orderObject.getCartProduct().getProducts().size()+"",cartProduct.getSalePrice()+"");
                    Snackbar.make(contentFrame, "thanks for your payment of " + this.getString(R.string.currency) + cartProduct.getSalePrice(), Snackbar.LENGTH_LONG).show();
                    // TODO: send 'confirm' to your server for verification

                    clearCart();

                    /* SharedPreferenceUtils prefs = new SharedPreferenceUtils(this);*/
                    prefs.setCartCount(0);
                    cartCount = prefs.getCartCount();
                    invalidateOptionsMenu();
                    inflatePaymentConfirmation();
                    menuItem.setIcon(ConverterCartBadge.convertLayoutToImage(this, cartCount, R.drawable.ic_shopping_cart));
                    paymentSuccess = true;
                } catch (JSONException e) {
                    Log.e("sampleapp", "no confirmation data: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            notifyUser(getString(R.string.payment_cancelled));
            inflateCart();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            inflateCart();
            Log.i("sampleapp", "Invalid payment / config set");
        }
    }

    private void clearCart() {
        for (int i = 0; i < cartProduct.getProducts().size(); i++) {
            Product product = cartProduct.getProducts().get(i);
            ProductApi productApi = new ProductApi();
            productApi.deleteProductCart(this, product, true);
        }
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
    }


    @Override
    public void onCartDelted(CartProduct product) {
        cartCount--;
        this.cartProduct = product;
        cartProduct.setTotalPrice();
        upDateCart(cartProduct);
        invalidateOptionsMenu();
        if (cartProduct.getProducts().size() == 0) {
            cartEmpty = true;
            hideCart();
            inflateEmptyFragment();
        }
        //  setTimeout();
    }

    @Override
    public void onCartLoaded(CartProduct product) {
        this.cartProduct = product;
        if (alert != null)
            alert.stopAlert();
        if (cartProduct != null && cartProduct.getProducts() != null) {
            cartProduct.setTotalPrice();
            cartCount = cartProduct.getProducts().size();
            cartEmpty = false;
            inflateCart();
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onCartLoadedEmpty() {
        cartEmpty = true;
        if (alert != null)
            alert.stopAlert();
        inflateEmptyFragment();
    }

    @Override
    public void onCheckoutCartEmptied() {
    }


    @Override
    public void onCheckOut(String amount) {
        hideCart();
        inflateShipping();
    }

    @Override
    public void onDeleteCartItem(Product product) {
        dialogDeleteProduct(product);
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
        payment(cartProduct.getSalePrice() + "");
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
