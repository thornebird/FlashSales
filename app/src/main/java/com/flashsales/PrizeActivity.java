package com.flashsales;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flashsales.Utils.AnswersEvents;
import com.flashsales.Utils.Configs;
import com.flashsales.Utils.FBEvents;
import com.flashsales.Utils.FirebaseEvents;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.dao.FreeOrderDao;
import com.flashsales.dao.OrderDao;
import com.flashsales.datamodel.FlashSaleTimer;
import com.flashsales.datamodel.OrderObject;
import com.flashsales.datamodel.PaymentObject;
import com.flashsales.datamodel.PaypalClient;
import com.flashsales.datamodel.PaypalResponse;
import com.flashsales.datamodel.PrizeOrderObject;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ShippingAddress;
import com.flashsales.datamodel.User;
import com.flashsales.fragments.FragmentCart;
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
import java.util.Calendar;

public class PrizeActivity extends AppCompatActivity implements FragmentShipping.OnAddressEvent {


    public final static String KEY_PRODUCT = "keyProduct";

    private Product freeProduct;
    private FragmentShipping fragmentShipping;
    private FragmentCart fragmentCart;
    private ShippingAddress shippingAddress;
    private Dialog dialogCheckout;
    private boolean isExit = false;
    private long prizeMills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prize);



        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,2);

        long calendMills = calendar.getTimeInMillis();
        prizeMills = calendMills - System.currentTimeMillis();

        if (getIntent().getExtras() != null) {
            freeProduct = getIntent().getParcelableExtra(KEY_PRODUCT);
        }

        init();
        inflateShipping();


    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if(!isExit) {
            dialogConfirmExitPrize();
        }else {
          finish();
        }
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.prize_summary));

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_content);
    }

    private void inflateShipping() {
        if (!isFinishing()) {
            if (fragmentShipping == null)
                fragmentShipping = FragmentShipping.newInstance(5.5);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frame_content, fragmentShipping).commit();
        }
    }


    private void hideShipping() {
        if (fragmentShipping != null && !isFinishing()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentShipping).commit();
        }
    }


    /*private void inflateCart(){
        if(!isFinishing()){
            if(fragmentCart == null)
                fragmentCart = FragmentCart.newInstance();

        }
    }*/

    public void dialogConfirmExitPrize() {
        final Dialog dialog = new Dialog(this);
        final View view = this.getLayoutInflater().inflate(R.layout.dialog_confrim_exit, null);
        dialog.setContentView(view);

        ConstraintLayout constraintLayout = (ConstraintLayout) view.findViewById(R.id.layout_parent);

        ImageView imProduct = (ImageView) view.findViewById(R.id.iv_product);
        Picasso.with(this).load(freeProduct.getImages().get(0)).placeholder(R.drawable.logo).into(imProduct);
        TextView tvTimer = (TextView) view.findViewById(R.id.tv_timer);
       if(prizeMills<System.currentTimeMillis()) {
           FlashSaleTimer flashSaleTimer = new FlashSaleTimer();
           flashSaleTimer.startTimer(prizeMills, tvTimer, getString(R.string.prize_expires));
       }else {
           tvTimer.setText(getString(R.string.time_expired));
       }


        TextView tvSure = (TextView) view.findViewById(R.id.tv_message);
        tvSure.setText(getString(R.string.leave_prize_behind));


        ImageView ivClose = (ImageView) view.findViewById(R.id.iv_delete);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        Button btnExit = (Button) view.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                System.exit(0);
            }
        });

        Button btnCheckout = (Button) view.findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogCheckout();
            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        isExit = true;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(
                    PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    //notifyUser(getString(R.string.payment_succesfull));
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    final Gson gson = gsonBuilder.create();
                    PaymentObject paymentObject = gson.fromJson(confirm.toJSONObject().toString(4), PaymentObject.class);

                    JSONObject jsonObjectResult = confirm.toJSONObject();
                    JSONObject jsonClient = jsonObjectResult.getJSONObject("client");
                    JSONObject jsonResponse = jsonObjectResult.getJSONObject("response");

                    PaypalResponse paypalResponse = gson.fromJson(jsonResponse.toString(4), PaypalResponse.class);
                    PaypalClient paypalClient = gson.fromJson(jsonClient.toString(4), PaypalClient.class);
                    //Thank you for payment page needed
                    paymentObject.setClient(paypalClient);
                    paymentObject.setPaypalResponse(paypalResponse);
                    Log.i("sampleapp", confirm.toJSONObject().toString(4));

                    freeProduct.setPrice("0");
                    freeProduct.setShippingPrice("5.5");
                    PrizeOrderObject prizeOrderObject = new PrizeOrderObject();
                    prizeOrderObject.setFreeProduct(freeProduct);
                    prizeOrderObject.setAddress(shippingAddress);
                    prizeOrderObject.setPaymentObject(paymentObject);
                    prizeOrderObject.getFreeProduct().setShortName(freeProduct.getShortName());

                    SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils(this);
                    User user = sharedPreferenceUtils.getUser();
                    prizeOrderObject.setUser(user);
                    prizeOrderObject.setAddress(shippingAddress);
                    prizeOrderObject.setEmailIdentifier(user.getEmail());

                    FreeOrderDao freeOrderDao = new FreeOrderDao();
                    freeOrderDao.uploadFreeOrder(prizeOrderObject);

                    hideShipping();
                    if (dialogCheckout != null)
                        dialogCheckout.dismiss();
                    inflatePaymentConfirmation();

                       FBEvents fbEvents = new FBEvents(MyApplication.getInstance());
                       AnswersEvents answersEvents = new AnswersEvents();
                       FirebaseEvents firebaseEvents = new FirebaseEvents(this);


                        fbEvents.logAddedToCartEvent(Configs.KEY_PRODUCT_EVENT_FREE + " " + freeProduct.getName(), freeProduct.getBrand(), freeProduct.getRetailPrice(),
                                getString(R.string.currency), Double.parseDouble(freeProduct.getPrice()));
                        answersEvents.atdEvent(Double.valueOf(freeProduct.getPrice()),Configs.KEY_PRODUCT_EVENT_FREE + " " + freeProduct.getName(), freeProduct.getBrand());
                        firebaseEvents.atcEvent(freeProduct.getName(),Configs.KEY_PRODUCT_EVENT_FREE + " " + freeProduct.getBrand(), freeProduct.getPrice());


                    invalidateOptionsMenu();
                    //
                    // menuItem.setIcon(ConverterCartBadge.convertLayoutToImage(this, cartCount, R.drawable.ic_shopping_cart));
                    //paymentSuccess = true;
                } catch (JSONException e) {
                    Log.e("sampleapp", "no confirmation data: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // notifyUser(getString(R.string.payment_cancelled));
            // inflateCart();
            Log.i("sampleapp", "Invalid payment / cancelled set");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            // inflateCart();
            Log.i("sampleapp", "Invalid payment / config set");
        }
    }

    private void dialogCheckout() {
        dialogCheckout = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_checkout_prize, null);
        dialogCheckout.setContentView(view);
       // dialogCheckout.setCancelable(false);

        ImageView ivProduct = (ImageView) view.findViewById(R.id.iv_product);
        Picasso.with(this).load(freeProduct.getImages().get(0)).placeholder(R.drawable.logo).into(ivProduct);


        TextView tvRetail = (TextView) view.findViewById(R.id.tv_retail_price);
        TextView tvShipping = (TextView) view.findViewById(R.id.tv_shipping_price);
        TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
        TextView tvTimer = (TextView)view.findViewById(R.id.tv_timer);

        Button btnCheckout = (Button) view.findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOut();
            }
        });

        if(prizeMills<System.currentTimeMillis()) {
            FlashSaleTimer flashSaleTimer = new FlashSaleTimer();
            flashSaleTimer.startTimer(prizeMills, tvTimer, getString(R.string.prize_expires));
        }else {
            tvTimer.setText(getString(R.string.time_expired));
        }

        tvMessage.setText(getString(R.string.congrats_completed_steps));
        tvRetail.setText(getString(R.string.retail_price) + " " + getString(R.string.currency) + freeProduct.getRetailPrice());
        tvRetail.setPaintFlags(tvRetail.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvShipping.setText(getString(R.string.shipping_price) + " " + getString(R.string.currency) + 5.5);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCheckout.getWindow().getAttributes());
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;

        dialogCheckout.show();
        dialogCheckout.getWindow().setAttributes(lp);
    }

    private void checkOut() {
        PayPalConfiguration config = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(Configs.PAYPAL_CLIENT_ID_RELEASE);

        Intent serviceConfig = new Intent(this, PayPalService.class);
        serviceConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(serviceConfig);

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(5.5), "USD", "Shipping",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent paymentConfig = new Intent(this, PaymentActivity.class);
        paymentConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        paymentConfig.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(paymentConfig, 0);

    }


    private void inflatePaymentConfirmation() {
        if (!isFinishing()) {
            FragmentPaymentConfirmation fragmentPaymentConfirmation = FragmentPaymentConfirmation.newInstance(5.5 + "");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frame_content, fragmentPaymentConfirmation).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.mi_cart);
        menuItem.setVisible(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddressSave(ShippingAddress address) {
        shippingAddress = address;
        SharedPreferenceUtils prefs = new SharedPreferenceUtils(this);
        prefs.saveAddress(shippingAddress);
       /* if (shippingAddress != null) {
            hideShipping();
        }*/

        dialogCheckout();

    }


}
