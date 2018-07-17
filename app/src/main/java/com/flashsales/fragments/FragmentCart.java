package com.flashsales.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flashsales.R;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.adapters.AdapterRecyclerView;
import com.flashsales.dao.ProductApi;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.FlashSaleTimer;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;
import com.flashsales.datamodel.ShippingAddress;

import java.util.ArrayList;
import java.util.Currency;

public class FragmentCart extends Fragment implements AdapterRecyclerView.ClickListener,
        View.OnClickListener {

    private OnCartEvent mListener;
    private CartProduct cartProduct;
    private int layoutResId = R.layout.item_cart;
    public final static String KEY_PRODUCTS = "products";
    public final static String KEY_CARTEMPTY = "carteEmpty";
    private AdapterRecyclerView adapterRecyclerView;
    private TextView tvRetailPrice,tvSalePrice,tvExpires,tvAddress;
    private int lastPosition = 0;
    private String retail, sale,cartExpires;
    private ShippingAddress address;
    private String currency;
    private boolean cartEmpty;

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

    public static FragmentCart newInstance(CartProduct cartProduct,boolean cartEmpty) {
        FragmentCart fragmentCart = new FragmentCart();
        Bundle args = new Bundle();
        args.putParcelable(KEY_PRODUCTS, cartProduct);
        args.putBoolean(KEY_CARTEMPTY,cartEmpty);
        fragmentCart.setArguments(args);
        return fragmentCart;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.cartProduct = args.getParcelable(KEY_PRODUCTS);
        this.cartEmpty = args.getBoolean(KEY_CARTEMPTY);
        this.retail = getActivity().getResources().getString(R.string.retail_price);
        this.sale = getActivity().getResources().getString(R.string.sale_price);
        this.cartExpires = getActivity().getResources().getString(R.string.cart_expires_in);
        this.currency = getActivity().getResources().getString(R.string.currency);
        SharedPreferenceUtils sharedPreferenceUtils = new SharedPreferenceUtils(getActivity());
        this.address = sharedPreferenceUtils.getAddress();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        RecyclerView rvCart = (RecyclerView) rootView.findViewById(R.id.rv_cart);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        rvCart.setLayoutManager(layoutManager);

        tvAddress = (TextView)rootView.findViewById(R.id.tv_address);
        tvAddress.setOnClickListener(this);
        tvRetailPrice = (TextView)rootView.findViewById(R.id.tv_retail_price);
        tvRetailPrice.setPaintFlags(tvRetailPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tvExpires  = (TextView) rootView.findViewById(R.id.tv_expires);
        tvSalePrice = (TextView)rootView.findViewById(R.id.tv_price);

        if(!cartEmpty || cartProduct.getProducts() == null) {
            adapterRecyclerView = new AdapterRecyclerView(getContext(), layoutResId, cartProduct.getProducts(), this);
            rvCart.setAdapter(adapterRecyclerView);
            tvExpires.setText(cartExpires+" "+cartProduct.getExpiresInMinutes());
            tvRetailPrice.setText(retail+" "+cartProduct.getRetailPrice()+"");
            tvSalePrice.setText(sale+" "+cartProduct.getSalePrice()+"");
            startCartTimer();
        }


        if(!address.getShippingAddress().equals("")){
            tvAddress.setText(address.getApt()+" "+address.getShippingAddress());
        }

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
        if(mListener!= null)
            mListener.onAddCartItem(product);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_checkout:

                break;

            case R.id.tv_address:
                if(mListener!=null)
                    mListener.onSetAddress();

                break;
        }
    }


    public void updateCart(final CartProduct cartProduct) {
        this.cartProduct = cartProduct;
        if(getActivity()!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(cartProduct !=null) {
                        adapterRecyclerView.updateList(cartProduct.getProducts());
                    }
                    adapterRecyclerView.notifyDataSetChanged();
                    tvRetailPrice.setText(retail + " "+currency+ cartProduct.getRetailPrice() + "");
                    tvSalePrice.setText(sale + " " +currency+cartProduct.getSalePrice() + "");
                }
            });
        }
    }


    private void startCartTimer(){
        if(cartProduct.getExpiresOn() != null) {
            FlashSaleTimer flashSaleTimer = new FlashSaleTimer();
            long expiryMills = Long.valueOf(cartProduct.getExpiresOn());
            flashSaleTimer.cartTimer(tvExpires, expiryMills, cartExpires);
        }
    }

    public interface OnCartEvent {
        public void onCheckOut();
        public void onDeleteCartItem(Product product);
        public void onAddCartItem(Product product);
        public void onSetAddress();

        //   public void onProductOpened(Product product);
    }


}
