package com.flashsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.flashsales.ProductActivity;
import com.flashsales.R;
import com.flashsales.adapters.AdapterRecyclerView;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;

public class FragmentProducts extends Fragment implements View.OnClickListener {

    public final static String LIST_PRODUCT = "listProduct";
    public final static String LIST_CART = "listCart";
    private ArrayList<ProductDisplay> products;
    private AdapterRecyclerView adapterRecyclerView;
    //private RecyclerView rvCartPreview;
    //  private FrameLayout frameCart;
    // private FragmentProductsEvent mListener;
    private boolean showCart;
    private TextView tvVisible;
    private String show;


    public FragmentProducts() {
    }

    public static FragmentProducts newInstance(ArrayList<ProductDisplay> productList) {
        FragmentProducts fragment = new FragmentProducts();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST_PRODUCT, productList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.products = args.getParcelableArrayList(LIST_PRODUCT);
        this.show = getContext().getResources().getString(R.string.show_cart);
        Log.d("FragmentProducts", "onCreate");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_products_grid, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
        if(products!=null && products.size()!=0) {
            adapterRecyclerView = new AdapterRecyclerView(getActivity().getBaseContext(), R.layout.row_product, products, new AdapterRecyclerView.ClickListener() {
                @Override
                public void onItemAdapterClick(Object object, int position, View view) {
                    intentOpen(object);
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
        }

        rv.setAdapter(adapterRecyclerView);
        rv.setLayoutManager(new GridLayoutManager(getActivity().getBaseContext(), 2));




       /* FragmentTopSales fragmentTopSales = FragmentTopSales.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.frameLayout,fragmentTopSales).commit();*/


        return view;
    }

    private void intentOpen(Object object) {
        ProductDisplay productDisplay = (ProductDisplay) object;

        Intent intent = new Intent(getActivity(), ProductActivity.class);
        intent.putExtra(ProductActivity.KEY_PRODUCT, productDisplay);
        startActivity(intent);
        //getActivity().overridePendingTransition(R.anim.right_to_left,R.anim.left_to_right);
    }

    public void updateList(ArrayList<ProductDisplay> productListNew) {
        if(adapterRecyclerView!=null) {
            adapterRecyclerView.updateList(productListNew);
            adapterRecyclerView.notifyDataSetChanged();
        }

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }




}
