package com.flashsales.fragments;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flashsales.R;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.adapters.AdapterRecyclerView;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.FlashSaleTimer;
import com.flashsales.datamodel.Product;

public class FragmentCart extends Fragment implements AdapterRecyclerView.ClickListener,
        View.OnClickListener {

    private OnCartEvent mListener;
    private CartProduct cartProduct;
    private int layoutResId = R.layout.item_cart;
    public final static String KEY_PRODUCTS = "products";
    public final static String KEY_CARTEMPTY = "carteEmpty";
    public final static String KEY_IS_FREE = "keyIsFree";
    public final static String KEY_IS_EXPIRY = "keyExpiry";
    private AdapterRecyclerView adapterRecyclerView;
    private ConstraintLayout layoutDiscountClaimed;
    private TextView tvRetailPrice, tvSalePrice, tvExpires,tvDiscountPercent;
    private int lastPosition = 0;
    private String retail, sale, cartExpires;
    //   private ShippingAddress address;
    private String currency;
    private boolean isFree;
    private boolean cartEmpty;
    private long millsExpiry;

    public FragmentCart() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnCartEvent) context;
        } catch (ClassCastException ex) {

        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public static FragmentCart newInstance(CartProduct cartProduct, boolean cartEmpty,boolean isFree,long millsExpiry) {
        FragmentCart fragmentCart = new FragmentCart();
        Bundle args = new Bundle();
        args.putParcelable(KEY_PRODUCTS, cartProduct);
        args.putBoolean(KEY_CARTEMPTY, cartEmpty);
        args.putBoolean(KEY_IS_FREE,isFree);
        args.putLong(KEY_IS_EXPIRY,millsExpiry);
        fragmentCart.setArguments(args);
        return fragmentCart;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.cartProduct = args.getParcelable(KEY_PRODUCTS);
        this.cartEmpty = args.getBoolean(KEY_CARTEMPTY);
        this.isFree = args.getBoolean(KEY_IS_FREE);
        this.millsExpiry = args.getLong(KEY_IS_EXPIRY);
        this.retail = getActivity().getResources().getString(R.string.retail_price);
        this.sale = getActivity().getResources().getString(R.string.sale_price);
        this.cartExpires = getActivity().getResources().getString(R.string.cart_expires_in);
        this.currency = getActivity().getResources().getString(R.string.currency);
        SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils(getActivity());
        // this.address = sharedPreferenceUtils.getAddress();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        layoutDiscountClaimed = (ConstraintLayout)rootView.findViewById(R.id.layout_discount_claimed);

        RecyclerView rvCart = (RecyclerView) rootView.findViewById(R.id.rv_cart);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        rvCart.setLayoutManager(layoutManager);

       /* tvAddress = (TextView)rootView.findViewById(R.id.tv_address);
        tvAddress.setOnClickListener(this);*/
        tvRetailPrice = (TextView) rootView.findViewById(R.id.tv_retail_price);
        tvRetailPrice.setPaintFlags(tvRetailPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvExpires = (TextView) rootView.findViewById(R.id.tv_expires);
        tvSalePrice = (TextView) rootView.findViewById(R.id.tv_price);
        tvDiscountPercent = (TextView)rootView.findViewById(R.id.tv_shippig_price);
        //tvEmpty = (TextView) rootView.findViewById(R.id.tv_cart_empty);

        if (!cartEmpty || cartProduct.getProducts() == null && !isFree) {
            adapterRecyclerView = new AdapterRecyclerView(getContext(), layoutResId, cartProduct.getProducts(), this);
            rvCart.setAdapter(adapterRecyclerView);
            tvExpires.setText(cartExpires + " " + cartProduct.getExpiresInMinutes());
            tvRetailPrice.setText(retail + " " + currency + cartProduct.getRetailPrice() + "");
            tvSalePrice.setText(sale + " " + currency + cartProduct.getSalePrice() + "");
            startCartTimer();
        }else if(isFree){
            double price  = 5.5;
            tvRetailPrice.setText(sale + " " + currency + cartProduct.getRetailPrice() + "");
            tvSalePrice.setText(getString(R.string.shipping_price) + " " + currency + price + "");
            FlashSaleTimer flashSaleTimer = new FlashSaleTimer();
            flashSaleTimer.startTimer(millsExpiry,tvExpires,getString(R.string.prize_expires));
        }
      /*  if (cartEmpty) {
            setTvEmpty();
        }*/


        /*if(!address.getShippingAddress().equals("")){
            tvAddress.setText(address.getApt()+" "+address.getShippingAddress());
        }*/

        Button btnCheckout = (Button) rootView.findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onItemAdapterClick(Object object, int position, View view) {
        // Product product = Product.class.cast(object);

    }

    @Override
    public void onCartItemDelted(Product product, int layoutPosition) {
        lastPosition = layoutPosition;
        if (mListener != null)
            mListener.onDeleteCartItem(product);
    }

    @Override
    public void onCartItemAdded(Product product) {
        if (mListener != null)
            mListener.onAddCartItem(product);
    }

    @Override
    public void onFavoruriteProductDelete(Product product) {

    }


    public void updateSalesPrices(double price,String percentDiscount){
        cartProduct.setSalePrice(price);
        tvSalePrice.setText( getString(R.string.sales_price_discount)+" "+getString(R.string.currency)+price);



        Animation animShake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        tvSalePrice.startAnimation(animShake);

        layoutDiscountClaimed.setVisibility(View.VISIBLE);
        tvDiscountPercent.setText(percentDiscount+"%");
        Animation animFadeIn = AnimationUtils.loadAnimation(getActivity(),R.anim.fade_in);
        layoutDiscountClaimed.startAnimation(animFadeIn);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_checkout:
                if (mListener != null /*&& address!=null*/) {
                    mListener.onCheckOut(cartProduct.getSalePrice() + "");
                }
                break;
          /*  case R.id.tv_cart_empty:
                Intent intentShopping = new Intent(getActivity(), MainActivity.class);
                startActivity(intentShopping);
                getActivity().finish();
                break;
*/
            /*case R.id.tv_address:
                if(mListener!=null)
                    mListener.onSetAddress();

                break;
*/
        }
    }


    public void updateCart(final CartProduct cartProduct) {
        this.cartProduct = cartProduct;
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (cartProduct != null) {
                        adapterRecyclerView.updateList(cartProduct.getProducts());
                    }
                  /*  if (cartProduct.getProducts().size() == 0) {
                        setTvEmpty();
                    }*/
                    adapterRecyclerView.notifyDataSetChanged();
                    tvRetailPrice.setText(retail + " " + currency + cartProduct.getRetailPrice() + "");
                    tvSalePrice.setText(sale + " " + currency + cartProduct.getSalePrice() + "");
                }
            });
        }
    }
/*
    private void setTvEmpty() {
        tvEmpty.setClickable(true);
        tvEmpty.setOnClickListener(this);
        tvEmpty.setVisibility(View.VISIBLE);
    }*/

    private void startCartTimer() {
        if (cartProduct.getExpiresOn() != null) {
            FlashSaleTimer flashSaleTimer = new FlashSaleTimer();
            long expiryMills = Long.valueOf(cartProduct.getExpiresOn());
            flashSaleTimer.cartTimer(tvExpires, expiryMills, cartExpires);
        }
    }




    public interface OnCartEvent {
        public void onCheckOut(String amount);
        public void onDeleteCartItem(Product product);
        public void onAddCartItem(Product product);
        public void onSetAddress();

        //   public void onProductOpened(Product product);
    }


}
