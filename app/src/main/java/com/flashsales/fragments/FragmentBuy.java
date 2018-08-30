package com.flashsales.fragments;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flashsales.R;
import com.flashsales.datamodel.FlashSaleTimer;

public class FragmentBuy extends Fragment implements View.OnClickListener {

    private OnBuyEvent mListener;
    private FlashSaleTimer saleTimer;
    private final static String KEY_PRICE = "price";
    private final static String KEY_DISCOUNT = "discount";
    private final static String KEY_ISFREE = "isFree";
    private String price;
    private String discount;
    private LinearLayout layoutQauntity;
    private EditText etAmount;
    private int amount = 0;
    private boolean isFree;

    public FragmentBuy() {
    }

    public static FragmentBuy newInstance(String price, String discount,boolean isFree) {
        FragmentBuy fragmentPopSale = new FragmentBuy();
        Bundle args = new Bundle();
        args.putString(KEY_PRICE, price);
        args.putString(KEY_DISCOUNT, discount);
        args.putBoolean(KEY_ISFREE,isFree);
        fragmentPopSale.setArguments(args);
        return fragmentPopSale;
    }

    /**
     * Called when a fragment_dialog_addtocart is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       try{
           mListener = (OnBuyEvent) context;
       }catch (ClassCastException ex){
           
       }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        price = args.getString(KEY_PRICE);
        discount = args.getString(KEY_DISCOUNT);
        isFree = args.getBoolean(KEY_ISFREE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_buy, container, false);

        TextView tvTimer = (TextView) rootView.findViewById(R.id.tv_timer);
        setSaleTimer(tvTimer);

        TextView tvPrice = (TextView) rootView.findViewById(R.id.tv_price);


        TextView tvDiscount = (TextView) rootView.findViewById(R.id.tv_discount);

        tvDiscount.setPaintFlags(tvDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Button btnBuy = (Button) rootView.findViewById(R.id.btn_buy);
        btnBuy.setOnClickListener(this);

        String currency = getContext().getString(R.string.currency);
        tvDiscount.setText(currency + discount);

        if(!isFree){
            tvPrice.setText(currency + price);
        }else {
            tvPrice.setText(getString(R.string.free));
            btnBuy.setText(getString(R.string.claim_prize));
        }
        return rootView;
    }


    private void setSaleTimer(TextView textView) {
        saleTimer = new FlashSaleTimer();
        saleTimer.updateTimerMidnight(textView);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_buy:
                if (mListener != null)
                    mListener.onBuy();
                break;

        }
    }

    public interface OnBuyEvent {
        public void onBuy();
    }
}
