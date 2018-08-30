package com.flashsales.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flashsales.MyApplication;
import com.flashsales.ProductActivity;
import com.flashsales.R;
import com.flashsales.adapters.AdapterRecyclerView;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;

import java.util.ArrayList;

public class FragmentTopSales extends Fragment {

    private final static String KEY_DISPLAY_PRODUCTS = "keyDisplayProducts";

    private ArrayList<ProductDisplay>  displayProducts;
    public static FragmentTopSales newInstance() {
        FragmentTopSales fragment = new FragmentTopSales();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication application= MyApplication.getInstance();
        displayProducts =application.getTopDisplayProductSales();
        Log.d("display",displayProducts.toString());
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_top_sales,container,false);
        TextView tvRecommended = (TextView)view.findViewById(R.id.tv_popular);
        TextView tvCountRecommened = (TextView)view.findViewById(R.id.tv_count);

        RecyclerView rv = (RecyclerView)view.findViewById(R.id.rv);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        if(displayProducts.size()>0) {
            AdapterRecyclerView adapter = new AdapterRecyclerView(getContext(), R.layout.row_product, displayProducts, new AdapterRecyclerView.ClickListener() {
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
            adapter.setHasStableIds(true);
            rv.setAdapter(adapter);
            tvRecommended.setVisibility(View.VISIBLE);
            tvCountRecommened.setText(displayProducts.size()+" "+getString(R.string.items));

        }
        rv.setLayoutManager(layoutManager);
        rv.setNestedScrollingEnabled(false);


        return view;
    }

    private void intentOpen(Object object) {
        ProductDisplay productDisplay = (ProductDisplay) object;
        Intent intent = new Intent(getActivity(), ProductActivity.class);
        intent.putExtra(ProductActivity.KEY_PRODUCT, productDisplay);
        startActivity(intent);
    }
}
