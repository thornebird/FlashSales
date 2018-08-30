package com.flashsales;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.flashsales.Utils.AnswersEvents;
import com.flashsales.Utils.Configs;
import com.flashsales.Utils.FBEvents;
import com.flashsales.Utils.FirebaseEvents;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.Utils.Utils;
import com.flashsales.adapters.AdapterImageSlider;
import com.flashsales.dao.ProductApi;
import com.flashsales.dao.DBViewedProducts;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.FlashSaleTimer;
import com.flashsales.datamodel.Product;
import com.flashsales.adapters.AdapterFragmentPager;
import com.flashsales.datamodel.ProductDisplay;
import com.flashsales.datamodel.ProductVariant;
import com.flashsales.datamodel.Review;
import com.flashsales.fragments.FragmentExpandSwipe;
import com.flashsales.fragments.FragmentItemAdded;
import com.flashsales.fragments.FragmentBuy;
import com.flashsales.fragments.FragmentProductProfile;
import com.flashsales.fragments.FragmentShipping;
import com.flashsales.fragments.FragmentVarientSelection;
import com.flashsales.fragments.FragmentYoutube;
import com.squareup.picasso.Picasso;

import static com.flashsales.CartActivity.KEY_FREE_PRIZE;
import static com.flashsales.CartActivity.KEY_FREE_PRODUCT;


public class ProductActivity extends AppCompatActivity implements FragmentBuy.OnBuyEvent,
        FragmentProductProfile.OnProductEvent,
        FragmentYoutube.OnVideoEvent,
        FragmentVarientSelection.OnSelectionEvent,
        FragmentExpandSwipe.OnSwipeEvent,
        ProductApi.OnLoaded
        , NetworkChangeReciever.NetworkListener {

    private MyApplication myApplication;
    public final static String KEY_PRODUCT = "product";
    public final static String KEY_PRODUCT_ACTIVE = "productActive";
    private final static String KEY_ISVIDEO = "isVideoOpen";
    private ProductDisplay productDisplay;
    private Product activeProduct;
    private Object inComingObject;
    private FrameLayout frameBuy, frameLayoutBottom, frameLayout;
    private TextView tvViewing;
    private AdapterFragmentPager adapterFragmentPager;
    private FragmentBuy fragmentBuy;
    private FragmentYoutube fragmentYoutube;
    private FragmentItemAdded fragmentItemAdded;
    private FragmentVarientSelection fragmentVarientSelection;
    private FragmentExpandSwipe fragmentExpandSwipe;
    private FragmentShipping fragmentShipping;
    private boolean isVideoOpen = false;
    private int currentVariant = 0;
    private Handler handler;
    private Handler handlerTimer;
    private Runnable runnable;
    private Runnable runnableViewing;
    private boolean isCart = false;
    private ViewPager vp;
    private LinearLayout layoutParent;
    private ArrayList<Review> reviewArrayList;
    private ProgressAlert progressAlert;
    private SharedPreferenceUtils prefs;
    private int cartCount;
    private ErrorAlert errorAlert;
    private boolean errorInflated = false;
    private double fakeRating = 0.0;
    private FBEvents fbEvents;
    private int actionBarHeight;
    private ImageButton imCartToolbar;
    private boolean addedToCart = false;
    private boolean atcDialogActive = false;
    private boolean isFreeDialog = false;
    private boolean isConfirmExitDialog = false;
    private boolean isShared = /*true*/false;
    private boolean isEntered = false;
    private ProductApi productApi;
    private int countStep = 0;
    private CallbackManager callbackManager;
    private Calendar calendar;
    private long prizeMills;
    private long calendarMills;
    private String variantSelected = "";
    private int currentStep = /*1*/ 0;
    /* private boolean isExistConfirmed = false;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        productDisplay = getIntent().getExtras().getParcelable(KEY_PRODUCT);
        activeProduct = getIntent().getExtras().getParcelable(KEY_PRODUCT_ACTIVE);

        if (productDisplay.isFree()) {
            if (calendar == null) {
                calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, 5);
                calendarMills = calendar.getTimeInMillis();
            }
        }

        initViews();
        if (productDisplay != null)
            fragmentBuy();

        fakeRating = productDisplay.getFakeRating();
        if (fakeRating == 0.0) {
            final Random random = new Random();
            fakeRating = ThreadLocalRandom.current().nextDouble(3, 5);
        }
        myApplication = (MyApplication) getApplicationContext();
        myApplication.setListenerNetwork(this);
        myApplication.reorderReviews();
        reviewArrayList = myApplication.getReviews();

        prefs = new SharedPreferenceUtils(this);
        cartCount = prefs.getCartCount();
        //dialogAddedToCart();

        if (activeProduct == null) {
            progressAlert = new ProgressAlert(this, getResources().getString(R.string.loading),
                    productDisplay.getName());
            productApi = ProductApi.getInstance();
            productApi.setListener(this);
            productApi.okhttpRequestProduct(this, productDisplay.getBrand(), productDisplay.getName());
        }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.mi_cart);
        menuItem.setIcon(ConverterCartBadge.convertLayoutToImage(this, cartCount, R.drawable.ic_shopping_cart));

        if (cartCount != 0 && addedToCart) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    final View menuItemView = findViewById(R.id.mi_cart);
                    animateCart(menuItemView);
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_cart:
                loadCart();
                break;
            case android.R.id.home:
                if (!productDisplay.isFree()) {
                    finish();
                } else if (productDisplay.isFree()/* && !isExistConfirmed*/) {
                    dialogConfirmExit();
                } /*else if (productDisplay.isFree() && isExistConfirmed) {
                    Intent intent = new Intent(ProductActivity.this, MainActivity.class);
                    startActivity(intent);
                }*/
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
        if (!isVideoOpen && !isCart && !productDisplay.isFree()) {
            super.onBackPressed();
        } else if (productDisplay.isFree()/* && !isExistConfirmed*/) {
            dialogConfirmExit();
        }
    }


    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(productDisplay.getName());

        layoutParent = (LinearLayout) findViewById(R.id.layout_parent);
        vp = (ViewPager) findViewById(R.id.vp);
        if (activeProduct != null) {
            adapterFragmentPager = new AdapterFragmentPager(activeProduct, AdapterFragmentPager.STATE_MAIN_ACTIVITY
                    , getSupportFragmentManager(), productDisplay.isFree(), reviewArrayList);
            vp.setAdapter(adapterFragmentPager);
            vp.setOffscreenPageLimit(2);

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

            activeProduct.setFakeRating(fakeRating);
            if (productDisplay.isFree()) {
                dialogFreePrize();
            }
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp);

        frameBuy = (FrameLayout) findViewById(R.id.frame_layout_pops);
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayoutBottom = (FrameLayout) findViewById(R.id.frame_layout_bottom);


    }

    private void loadCart() {
        Intent intent = new Intent(ProductActivity.this, CartActivity.class);
        startActivity(intent);
    }

    private void fragmentBuy() {
        if (fragmentBuy == null) {
            fragmentBuy = FragmentBuy.newInstance(productDisplay.getPrice() + "",
                    productDisplay.getRetailPrice() + "",
                    productDisplay.isFree());
        }
        if (!isFinishing()) {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_up);
            transaction.add(R.id.frame_layout_pops, fragmentBuy, "fragmentBuy").commit();
        }

    }

    private void hideVideoFragment() {
        if (fragmentYoutube != null && !isFinishing()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentYoutube).commit();
            isVideoOpen = !isVideoOpen;
        }
    }

    private void inflateVariants() {
        if (fragmentVarientSelection == null) {
            fragmentVarientSelection = FragmentVarientSelection.newInstance(activeProduct);
        }
        if (!isFinishing()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.animator.slide_upv2, 0);
            transaction.add(R.id.frame_layout_bottom, fragmentVarientSelection).commit();
        }
    }

    private void hideVarients() {
        if (fragmentVarientSelection != null && !isFinishing()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(0, R.animator.slide_downv2);
            transaction.remove(fragmentVarientSelection).commit();

        }

    }


    private void inflateItemAdded() {
        fragmentItemAdded = FragmentItemAdded.newInstance(activeProduct);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_layout_bottom, fragmentItemAdded).commit();
    }

    private void hideItemAdded() {
        if (fragmentItemAdded != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentItemAdded).commit();
        }
    }

    private void inflateImageFragment() {
        if (fragmentExpandSwipe == null && !isFinishing()) {
            fragmentExpandSwipe = FragmentExpandSwipe.newInstance(activeProduct);
        }
        if (!isFinishing()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frame_layout, fragmentExpandSwipe).commit();
        }
    }

    private void hideImageFragment() {
        if (fragmentExpandSwipe != null && !isFinishing()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragmentExpandSwipe).commit();
        }
    }


    private void updateAlaram(long mills) {
        // long tenMinsLess = mills - 3540000;
        long tenMinsLess = mills - 900000;
        Intent intent = new Intent(ProductActivity.this, CartBroadCastReciever.class);
        intent.putExtra("requestCode", Utils.reqCodeCart);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Utils.reqCodeCart, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC_WAKEUP, tenMinsLess, pendingIntent);
    }


    private void notifyUser(String message, boolean isSuccess) {
        Snackbar bar = Snackbar.make(frameLayout, message, Snackbar.LENGTH_SHORT);
        if (isSuccess) {
            ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
            ProgressBar item = new ProgressBar(this);
            contentLay.addView(item);
        }
        bar.show();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBuy() {
        if (productDisplay.isFree()) {
            dialogFreePrize();
        } else {
            inflateVariants();
        }
    }

    @Override
    public void onExpandImage() {
        inflateImageFragment();
    }

    @Override
    public void itemSaved(String message, boolean isSuccess) {
        notifyUser(message, isSuccess);
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
        FBEvents fbEvents = new FBEvents(getApplication());
        fbEvents.logInitiatedCheckoutEvent(activeProduct.getName(), activeProduct.getBrand(), activeProduct.getPrice()
                , 1, false, getString(R.string.currency)
                , Double.parseDouble(activeProduct.getPrice()));
        AnswersEvents answersEvents = new AnswersEvents();
        answersEvents.checkoutStartedEvent(Double.valueOf(activeProduct.getPrice()), 1);

        FirebaseEvents firebaseEvents = new FirebaseEvents(this);
        firebaseEvents.checkoutEvent(activeProduct.getName(), activeProduct.getBrand(), activeProduct.getPrice());

        String message = getString(R.string.adding_to_cart);
        notifyUser(message, true);
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
    public void onProductLoaded(final Product productObject) {
        if (productObject != null) {
            activeProduct = productObject;
            activeProduct.setFakeRating(fakeRating);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    adapterFragmentPager = new AdapterFragmentPager(activeProduct,
                            AdapterFragmentPager.STATE_MAIN_ACTIVITY,
                            getSupportFragmentManager(),
                            productDisplay.isFree(), reviewArrayList);
                    vp.setAdapter(adapterFragmentPager);
                    vp.setOffscreenPageLimit(2);
                    adapterFragmentPager.notifyDataSetChanged();
                    if (progressAlert != null)
                        progressAlert.stopAlert();
                    if (productDisplay.isFree()) {
                        dialogFreePrize();
                    }

                }
            });
            DBViewedProducts viewedProducts = new DBViewedProducts(this);
            if (!viewedProducts.ifExists(activeProduct)) {
                activeProduct.setImage(activeProduct.getImagePaths().get(0));
                viewedProducts.addProduct(activeProduct);
            }
            FBEvents fbEvents = new FBEvents(getApplication());
            AnswersEvents answersEvents = new AnswersEvents();
            FirebaseEvents firebaseEvents = new FirebaseEvents(this);
            if (productDisplay.isFree()) {
                fbEvents.logViewedContentEvent(Configs.KEY_PRODUCT_EVENT_FREE + " " + activeProduct.getName(), activeProduct.getBrand(), activeProduct.getRetailPrice(), getString(R.string.currency), Double.valueOf(activeProduct.getPrice()));
                answersEvents.vcEvent(Configs.KEY_PRODUCT_EVENT_FREE + " " + activeProduct.getName(), activeProduct.getBrand(), activeProduct.getPrice());
                firebaseEvents.vcEvent(Configs.KEY_PRODUCT_EVENT_FREE + " " + activeProduct.getName(), activeProduct.getBrand(), activeProduct.getPrice() + "");

            } else {
                fbEvents.logViewedContentEvent(activeProduct.getName(), activeProduct.getBrand(), activeProduct.getRetailPrice(), getString(R.string.currency), Double.valueOf(activeProduct.getPrice()));
                answersEvents.vcEvent(activeProduct.getName(), activeProduct.getBrand(), activeProduct.getPrice());
                firebaseEvents.vcEvent(activeProduct.getName(), activeProduct.getBrand(), activeProduct.getPrice() + "");
            }


        } else {
            notifyUser(getString(R.string.loading_error), true);
            if (productApi == null) {
                productApi = ProductApi.getInstance();
                productApi.setListener(this);
            }
            productApi.okhttpRequestProduct(this, productDisplay.getBrand(), productDisplay.getName());
        }
    }

    private void animateCart(View view) {
        Animation animShake = AnimationUtils.loadAnimation(this, R.anim.shake);
        view.startAnimation(animShake);
        animShake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dialogAddedToCart();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void dialogAddedToCart() {
        if (!atcDialogActive) {

            try {
                final Dialog dialogCheckout = new Dialog(ProductActivity.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_added_to_cart, null);
                dialogCheckout.setContentView(view);
                ImageView ivProduct = (ImageView) view.findViewById(R.id.iv_product);
                Picasso.with(ProductActivity.this).load(activeProduct.getImages().get(0)).placeholder(R.drawable.logo).into(ivProduct);

                Button btnCheck = (Button) view.findViewById(R.id.btn_checkout);
                btnCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogCheckout.dismiss();
                        loadCart();
                    }
                });


                Button btnShop = (Button) view.findViewById(R.id.btn_continue_shopping);
                btnShop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogCheckout.dismiss();
                    }
                });

                ImageView ivClose = (ImageView) view.findViewById(R.id.iv_delete);
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogCheckout.dismiss();
                    }
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialogCheckout.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogCheckout.show();
                dialogCheckout.getWindow().setAttributes(lp);
                atcDialogActive = !atcDialogActive;
            } catch (RuntimeException ex) {
                Log.e("exception", ex.toString());
            }

        }
    }

    private void dialogFreePrize() {
        if (!isFreeDialog) {
            final Dialog dialog = new Dialog(ProductActivity.this);
            final View view = getLayoutInflater().inflate(R.layout.dialog_free_prize, null);
            dialog.setContentView(view);

            final ConstraintLayout layoutDialog = (ConstraintLayout)view.findViewById(R.id.layout_dialog);
            prizeMills = calendarMills - System.currentTimeMillis();

            TextView tvTimer = (TextView) view.findViewById(R.id.tv_timer);
            if (prizeMills < System.currentTimeMillis()) {
                FlashSaleTimer flashSaleTimer = new FlashSaleTimer();
                flashSaleTimer.startTimer(prizeMills, tvTimer, getString(R.string.prize_expires));
            } else {
                tvTimer.setText(getString(R.string.time_expired));
            }

            // ImageView iv = (ImageView) view.findViewById(R.id.iv_product);
            // Picasso.with(ProductActivity.this).load(productDisplay.getImage()).placeholder(R.drawable.logo).into(iv);


            ViewPager vpImages = (ViewPager) view.findViewById(R.id.vp);
            AdapterImageSlider adapterImageSlider = new AdapterImageSlider(this, activeProduct.getImages(), new AdapterImageSlider.OnImageClick() {
                @Override
                public void onImageClick() {

                }
            });
            vpImages.setAdapter(adapterImageSlider);

            final ImageView[] imDots = new ImageView[adapterImageSlider.getCount()];
            LinearLayout layoutDots = (LinearLayout) view.findViewById(R.id.layout_slider_dots);
            addDotsView(layoutDots, imDots, vpImages.getCurrentItem());
            vpImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    for (ImageView im : imDots) {
                        im.setImageDrawable(ContextCompat.getDrawable(ProductActivity.this, R.drawable.non_active_dot));
                    }
                    imDots[position].setImageDrawable(ContextCompat.getDrawable(ProductActivity.this, R.drawable.active_dot));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            ImageView ivDelte = (ImageView) view.findViewById(R.id.iv_delete);
            ivDelte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    isFreeDialog = !isFreeDialog;
                }
            });

            Resources resources = getResources();
            String congrats = String.format(resources.getString(R.string.prize_stand_chance), productDisplay.getName());
            final String stepCount;
            if (activeProduct.getProductVariants() != null) {
                stepCount = "3";
                @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String variantChose = String.format(getString(R.string.choose), activeProduct.getParentVarient());
            } else {
                stepCount = "2";
            }


            String steps = String.format(this.getString(R.string.complete_steps_competition), stepCount);
            final TextView tvSteps = (TextView) view.findViewById(R.id.tv_steps);
            tvSteps.setText(steps);

            final TextView tvStepOne = (TextView) view.findViewById(R.id.tv_step_one);

            TextView tvCongrats = (TextView) view.findViewById(R.id.tv_congrats);
            tvCongrats.setText(congrats);

            final Button btnView = (Button) view.findViewById(R.id.btn_view);
            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    isFreeDialog = !isFreeDialog;
                }
            });

            final Button btnClaim = (Button) view.findViewById(R.id.btn_claim_prize);
            btnClaim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!isShared && currentStep == 0) {
                        share(btnClaim, tvStepOne,layoutDialog);
                    } else if (isShared && currentStep == 1) {

                        if (isShared && activeProduct.getParentVarient() == null
                                || isShared && activeProduct.getParentVarient() != null && !variantSelected.equals("") && !variantSelected.equals(activeProduct.getParentVarient())) {
                            dialog.dismiss();
                            Intent intent = new Intent(ProductActivity.this, PrizeActivity.class);
                            intent.putExtra(KEY_FREE_PRODUCT, activeProduct);
                            startActivity(intent);
                            finish();
                        } else if (activeProduct.getParentVarient() != null && variantSelected.equals("") || variantSelected.equals(activeProduct.getParentVarient())) {
                            Snackbar.make(view, getString(R.string.select_varient) + activeProduct.getParentVarient(), Snackbar.LENGTH_LONG).show();
                        }
                    }


                }
            });


            dialog.setOnKeyListener(new Dialog.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface arg0, int keyCode,
                                     KeyEvent event) {
                    // TODO Auto-generated method stub
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        isFreeDialog = !isFreeDialog;
                    }
                    return true;
                }
            });


            ConstraintLayout layoutParent = (ConstraintLayout) view.findViewById(R.id.layout_parent);
            ConstraintLayout layoutVariant = (ConstraintLayout) view.findViewById(R.id.frame_variant);
            Spinner spinnerVariant = (Spinner) view.findViewById(R.id.sp_variant);
            final TextView tvChooseVariant = (TextView) view.findViewById(R.id.tv_chose_variant);

            if (activeProduct.getParentVarient() != null) {

                final ArrayList<String> varints = variantCompetitionDialog();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, varints);

                spinnerVariant.setAdapter(adapter);
                spinnerVariant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        variantSelected = varints.get(position);
                        if (position != 0) {
                            activeProduct.setVarientSelected(variantSelected);
                            tvChooseVariant.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                        } else if (position == 0) {
                            tvChooseVariant.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_cancel, 0, 0, 0);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                if (!variantSelected.equals("")) {
                    int position = adapter.getPosition(variantSelected);
                    spinnerVariant.setSelection(position);
                }

            } else {
                layoutVariant.removeView(spinnerVariant);
                layoutVariant.removeView(tvChooseVariant);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(layoutParent);

                constraintSet.connect(tvStepOne.getId(), ConstraintSet.BOTTOM, tvSteps.getId(), ConstraintSet.BOTTOM);
                constraintSet.applyTo(layoutParent);

                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) layoutVariant.getLayoutParams();
                lp.height = 0;
                lp.width = 0;
                layoutVariant.setLayoutParams(lp);

                ConstraintLayout.LayoutParams lpParamsTvStepOne = (ConstraintLayout.LayoutParams) tvStepOne.getLayoutParams();
                lpParamsTvStepOne.setMargins(0, 80, 0, 0);
                tvStepOne.setLayoutParams(lpParamsTvStepOne);

            }


            if (isShared) {
                btnClaim.setText(getString(R.string.step_two));
                tvStepOne.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
            }


            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
            dialog.show();
            isFreeDialog = !isFreeDialog;

            SharedPreferenceUtils prefs = new SharedPreferenceUtils(this);
            prefs.setFreePrizeOffer(true);

        }
    }

    private void dialogConfirmExit() {
        if (!isConfirmExitDialog) {
            final Dialog dialog = new Dialog(this);
            View view = getLayoutInflater().inflate(R.layout.dialog_confrim_exit, null);
            dialog.setContentView(view);

            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        isConfirmExitDialog = false;
                    }
                    return true;
                }
            });

            ImageView imProduct = (ImageView) view.findViewById(R.id.iv_product);
            Picasso.with(this).load(activeProduct.getImages().get(0)).placeholder(R.drawable.logo).into(imProduct);
            TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
            tvMessage.setText(getString(R.string.confirm_exit_dialog));

            TextView tvTimer = (TextView) view.findViewById(R.id.tv_timer);
            if (prizeMills < System.currentTimeMillis()) {
                FlashSaleTimer flashSaleTimer = new FlashSaleTimer();
                flashSaleTimer.startTimer(prizeMills, tvTimer, getString(R.string.prize_expires));
            } else {
                tvTimer.setText(getString(R.string.last_chance_claim));
            }

            ImageView imClose = (ImageView) view.findViewById(R.id.iv_delete);
            imClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    isConfirmExitDialog = false;
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
                    isConfirmExitDialog = false;
                    dialogFreePrize();
                }
            });
            btnCheckout.setText(getString(R.string.continue_one));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());

            dialog.getWindow().setAttributes(lp);
            dialog.show();
            isConfirmExitDialog = true;
        }
    }


    private void addDotsView(LinearLayout dotsLayout, ImageView[] imDots, int currentDot) {
        for (int i = 0; i < imDots.length; i++) {
            imDots[i] = new ImageView(this);
            if (i == currentDot) {
                imDots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot));
            } else {
                imDots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.non_active_dot));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            dotsLayout.addView(imDots[i], params);
        }
    }

    private ArrayList<String> variantCompetitionDialog() {
        ArrayList<String> list = new ArrayList<>();
        list.add(activeProduct.getParentVarient());
        for (int i = 0; i < activeProduct.getProductVariants().get(0).getVariantValues().size(); i++) {
            String value = activeProduct.getProductVariants().get(0).getVariantValues().get(i).getValue();
            list.add(value);
        }

        return list;
    }

    private void share(final Button btn, final TextView tv, final ConstraintLayout layout) {

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            callbackManager = CallbackManager.Factory.create();
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.flashsales"))
                    .setQuote(getString(R.string.share_promotion) + " " + productDisplay.getName())
                    .build();

            ShareDialog shareDialog = new ShareDialog(this);
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    isShared = true;
                    currentStep = 1;
                    btn.setText(getString(R.string.step_two));
                    tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
                }

                @Override
                public void onCancel() {
                  Snackbar.make(layout, getString(R.string.cancel_share_action), Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {

                }
            });
            shareDialog.show(this, content);
        }
    }



  /*  private void dialogDeleteProduct(final Product product){
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);

        View view  = getLayoutInflater().inflate(R.layout.dialog_delete_product,null);
        dialog.setContentView(view);

        ImageView ivProduct = (ImageView)view.findViewById(R.id.iv_product);
        Picasso.with(this).load(product.getImage()).into(ivProduct);

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
                //deleteItem(product);
                *//*   productApi.deleteProductCart(getBaseContext(), product,false);*//*
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
*/


/*    private void setTimeoutAlarm(Context context, long timeout) {
        if (context == null || Long.valueOf(timeout) == null) {
            Log.d("timeout","ontext == null || Long.valueOf(timeout) == null");
            return;
        }
        Log.d("timeout","added");
        Intent intent = new Intent(context, TimeBroadcastReciever.class);
        intent.putExtra(Utils.keyTimeout, Utils.reqCodeTimeout);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Utils.reqCodeTimeout, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC_WAKEUP, *//*timeout*//*System.currentTimeMillis(), pendingIntent);
    }*/

    @Override
    public void onProductsLoaded(ArrayList<ProductDisplay> productDisplays) {

    }

    @Override
    public void onCartAdded(CartProduct cartProduct) {
        addedToCart = true;
        /*hideVarients();*/
        hideVarients();
        // dialogAddedToCart();
        cartCount = cartProduct.getProducts().size();
        invalidateOptionsMenu();
        long time = Long.parseLong(cartProduct.getExpiresOn());
        updateAlaram(time);

        FBEvents fbEvents = new FBEvents(myApplication);
        AnswersEvents answersEvents = new AnswersEvents();
        FirebaseEvents firebaseEvents = new FirebaseEvents(this);

        if (productDisplay.isFree()) {
            fbEvents.logAddedToCartEvent(Configs.KEY_PRODUCT_EVENT_FREE + " " + activeProduct.getName(), activeProduct.getBrand(), activeProduct.getRetailPrice(),
                    getString(R.string.currency), Double.parseDouble(activeProduct.getPrice()));
            answersEvents.atdEvent(Double.valueOf(activeProduct.getPrice()), Configs.KEY_PRODUCT_EVENT_FREE + " " + activeProduct.getName(), activeProduct.getBrand());
            firebaseEvents.atcEvent(activeProduct.getName(), Configs.KEY_PRODUCT_EVENT_FREE + " " + activeProduct.getBrand(), activeProduct.getPrice());
        } else {
            fbEvents.logAddedToCartEvent(activeProduct.getName(), activeProduct.getBrand(), activeProduct.getRetailPrice(),
                    getString(R.string.currency), Double.parseDouble(activeProduct.getPrice()));
            answersEvents.atdEvent(Double.valueOf(activeProduct.getPrice()), activeProduct.getName(), activeProduct.getBrand());
            firebaseEvents.atcEvent(activeProduct.getName(), activeProduct.getBrand(), activeProduct.getPrice());
        }


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

    @Override
    public void onCheckoutCartEmptied() {

    }

    @Override
    public void networkAvailable() {
        hideInternetError();
        if (activeProduct == null) {
            productApi.okhttpRequestProduct(this, productDisplay.getBrand(), productDisplay.getName());
        }

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
