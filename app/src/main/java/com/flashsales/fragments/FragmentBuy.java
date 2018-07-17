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
    private String price;
    private String discount;
    private LinearLayout layoutQauntity;
    private EditText etAmount;
    private int amount = 0;

    public FragmentBuy() {
    }

    public static FragmentBuy newInstance(String price, String discount) {
        FragmentBuy fragmentPopSale = new FragmentBuy();
        Bundle args = new Bundle();
        args.putString(KEY_PRICE, price);
        args.putString(KEY_DISCOUNT, discount);
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_buy, container, false);

        TextView tvTimer = (TextView) rootView.findViewById(R.id.tv_timer);
        setSaleTimer(tvTimer);

        TextView tvPrice = (TextView) rootView.findViewById(R.id.tv_price);
        String currency = getContext().getString(R.string.currency);
        tvPrice.setText(currency + price);

        TextView tvDiscount = (TextView) rootView.findViewById(R.id.tv_discount);
        tvDiscount.setText(currency + discount);
        tvDiscount.setPaintFlags(tvDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Button btnBuy = (Button) rootView.findViewById(R.id.btn_buy);
        btnBuy.setOnClickListener(this);
/*
        ImageView imMinus = (ImageView) rootView.findViewById(R.id.im_minus);
/        imMinus.setOnClickListener(this);*/

      /*  ImageView imAdd = (ImageView) rootView.findViewById(R.id.im_add);
        imAdd.setOnClickListener(this);*/

        /*etAmount = (EditText) rootView.findViewById(R.id.et_amount);
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        /*layoutQauntity = (LinearLayout) rootView.findViewById(R.id.layout_addtocart);
       // layoutQauntity.setVisibility(View.INVISIBLE);
        Animation slideUpAnim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.slide_up);
        rootView.startAnimation(slideUpAnim);*/

        return rootView;
    }


    private void setSaleTimer(TextView textView) {
        saleTimer = new FlashSaleTimer();
        saleTimer.updateTimerMidnight(textView);
    }
/*
    private void addAmount() {
        amount++;
        etAmount.setText(amount + "");
    }

    private void removeAmount() {
        if (amount != 0)
            amount--;
        etAmount.setText(amount + "");
    }*/

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
