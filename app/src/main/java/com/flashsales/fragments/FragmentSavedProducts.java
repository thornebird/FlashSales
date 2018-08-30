package com.flashsales.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.flashsales.MainActivity;
import com.flashsales.R;
import com.flashsales.adapters.AdapterRecyclerView;
import com.flashsales.dao.OrderDao;
import com.flashsales.datamodel.OrderObject;
import com.flashsales.datamodel.Product;

import java.util.ArrayList;

public class FragmentSavedProducts extends Fragment implements AdapterRecyclerView.ClickListener, View.OnClickListener {

    private OnViwedProductEvent mListener;

    public final static String KEY_PRODUCT = "keyProduct";
    private final static String KEY_STATE = "keyState";
    private ArrayList<Product> products;
    private AdapterRecyclerView adapterRecyclerView;
    private int currentState = 0;
    public final static int STATE_VIWIED = 0;
    public final static int STATE_FAVOURITES = 1;

    public static FragmentSavedProducts newInstance(ArrayList<Product> products, int state) {
        FragmentSavedProducts fragment = new FragmentSavedProducts();
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_PRODUCT, products);
        args.putInt(KEY_STATE, state);
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
        try {
            mListener = (OnViwedProductEvent) context;
        } catch (ClassCastException ex) {
            Log.e("ClassCastException", ex.toString());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        products = args.getParcelableArrayList(KEY_PRODUCT);
        currentState = args.getInt(KEY_STATE);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewiedproducts, container, false);
        //ConstraintLayout parentLayout = (ConstraintLayout)view.findViewById(R.id.frame_parent);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frame_content);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        rv.setLayoutManager(layoutManager);
        adapterRecyclerView = new AdapterRecyclerView(getContext(), R.layout.item_viewed_product, products, this);
        rv.setAdapter(adapterRecyclerView);

        if (products.size() == 0 &&mListener!=null) {
            mListener.onCartEmptied();
        }

        return view;
    }

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

    @Override
    public void onFavoruriteProductDelete(Product product) {
        if (mListener != null)
            mListener.onDeleteFavouriteProduct(product);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_emptylist:
                intentShop();
                break;
        }
    }

    private void intentShop() {
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
    }

    public interface OnViwedProductEvent {
        public void onViewClicked(Product product);
        public void onDeleteFavouriteProduct(Product product);
        public void onCartEmptied();
    }
}
