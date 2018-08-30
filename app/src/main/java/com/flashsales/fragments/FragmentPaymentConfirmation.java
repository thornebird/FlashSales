package com.flashsales.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flashsales.HistoryActivity;
import com.flashsales.R;

public class FragmentPaymentConfirmation extends Fragment {

    private final static String KEY_AMOUNT = "keyAmount";
    private String amount;

    public static FragmentPaymentConfirmation newInstance(String amount){
        FragmentPaymentConfirmation fragment = new FragmentPaymentConfirmation();
        Bundle args= new Bundle();
        args.putString(KEY_AMOUNT,amount);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amount = getArguments().getString(KEY_AMOUNT);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_confirmation , container,false);

        Button btnViewOrders = (Button)view.findViewById(R.id.btn_view_order_history);
        btnViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                intent.putExtra(HistoryActivity.KEY_SCROLL_POS,HistoryActivity.POS_ORDER_HISTORY);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}

