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

import com.flashsales.R;
import com.flashsales.adapters.AdapterRecyclerView;
import com.flashsales.datamodel.Product;

import java.util.ArrayList;

public class FragmentViewedProducts extends Fragment {

    private OnViwedProductEvent mListener;

    public final static String KEY_PRODUCT = "keyProduct";
    private ArrayList<Product> products;

    public static FragmentViewedProducts newInstance(ArrayList<Product> products) {
        FragmentViewedProducts fragment = new FragmentViewedProducts();
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_PRODUCT, products);
        fragment.setArguments(args);
        return fragment;
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
        mListener = (OnViwedProductEvent) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        products = args.getParcelableArrayList(KEY_PRODUCT);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewiedproducts, container, false);

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        rv.setLayoutManager(layoutManager);
       /* rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), LinearLayoutManager.VERTICAL));*/
        AdapterRecyclerView adapterRecyclerView = new AdapterRecyclerView(getContext(), R.layout.item_viewed_product, products, new AdapterRecyclerView.ClickListener() {
            @Override
            public void onItemAdapterClick(Object object, int position, View view) {
                if (mListener != null) {
                    Product clickedProduct = (Product) object;
                    mListener.onViewClicked(clickedProduct);
                }
            }

            @Override
            public void onCartItemDelted(Product product, int layoutPosition) {

            }

            @Override
            public void onCartItemAdded(Product product) {

            }
        });
        rv.setAdapter(adapterRecyclerView);

        return view;
    }

    public interface OnViwedProductEvent {
        public void onViewClicked(Product product);
    }
}
