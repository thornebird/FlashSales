package com.flashsales.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flashsales.R;
import com.flashsales.adapters.AdapterRecyclerView;
import com.flashsales.datamodel.OrderObject;
import com.flashsales.datamodel.PrizeOrderObject;
import com.flashsales.datamodel.Product;

import java.util.ArrayList;

public class FragmentOrderHistory extends Fragment {
    private final static String KEY_ORDER_LIST = "keyOrderList";
    private final static String KEY_PRIZE_ORDER_LIST = "keyOrderListPrize";
    private ArrayList<OrderObject> orders;
    private ArrayList<PrizeOrderObject> prizeOrders;
    private OnOrderClicked mOrderListener;

    public static FragmentOrderHistory newInstance(ArrayList<OrderObject> orders, ArrayList<PrizeOrderObject> prizeOrderObjects) {
        FragmentOrderHistory fragmentOrderHistory = new FragmentOrderHistory();
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_ORDER_LIST, orders);
        args.putParcelableArrayList(KEY_PRIZE_ORDER_LIST, prizeOrderObjects);
        fragmentOrderHistory.setArguments(args);
        return fragmentOrderHistory;
    }

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOrderListener = (OnOrderClicked) context;
        } catch (ClassCastException ex) {

        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        orders = args.getParcelableArrayList(KEY_ORDER_LIST);
        prizeOrders = args.getParcelableArrayList(KEY_PRIZE_ORDER_LIST);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_history, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_order_history);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        rv.setLayoutManager(layoutManager);
        rv.setNestedScrollingEnabled(false);
        AdapterRecyclerView adapterRecyclerView = new AdapterRecyclerView(getContext(), R.layout.item_order_history, orders, new AdapterRecyclerView.ClickListener() {
            @Override
            public void onItemAdapterClick(Object object, int position, View view) {
                OrderObject order = (OrderObject) object;
                if (mOrderListener != null)
                    mOrderListener.OnOrderClicked(order);

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
        rv.setAdapter(adapterRecyclerView);

        if (prizeOrders != null) {
            if (prizeOrders.size() > 0) {
                RecyclerView rvPrize = (RecyclerView) rootView.findViewById(R.id.rv_prizeorder_history);
                LinearLayoutManager lm2 =  new LinearLayoutManager(getActivity().getBaseContext());
                rvPrize.setLayoutManager(lm2);
                rvPrize.setNestedScrollingEnabled(false);
                AdapterRecyclerView adapterPrizesRecyclerView = new AdapterRecyclerView(getContext(), R.layout.item_order_history, prizeOrders, new AdapterRecyclerView.ClickListener() {
                    @Override
                    public void onItemAdapterClick(Object object, int position, View view) {
                        PrizeOrderObject order = (PrizeOrderObject) object;
                        if (mOrderListener != null)
                            mOrderListener.OnFreeOrderClicked(order);

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
                rvPrize.setAdapter(adapterPrizesRecyclerView);
            }
        }
        return rootView;
    }

    public interface OnOrderClicked {
        public void OnOrderClicked(OrderObject orderObject);

        public void OnFreeOrderClicked(PrizeOrderObject prizeOrderObject);
    }
}
