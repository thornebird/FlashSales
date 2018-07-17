package com.flashsales.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

 import com.flashsales.R;
import com.flashsales.adapters.AdapterRecyclerView;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.Stock;

public class FragmentVariants extends Fragment implements View.OnClickListener, AdapterRecyclerView.ClickListener {

    private OnVarientEvent mListener;
    private int layoutResId;
    private ArrayList<Stock> productVariants;
    private final static String KEY_LAYOUT_ID = "layoutId";
    private final static String KEY_VARIANTS = "variants";
    private final static String KEY_PARENT = "parent";
    private String parent;
    private RecyclerView rvVariants;
    private TextView tvSelect;
    private ImageView imBack;
    private AdapterRecyclerView adapterRecyclerView;
    private int currentVariant;

    public FragmentVariants() {
    }

    public static FragmentVariants newInstance(int layoutResId,String parent, ArrayList<Stock> variant) {
        FragmentVariants fragmentAddToCart = new FragmentVariants();
        Bundle args = new Bundle();
        args.putInt(KEY_LAYOUT_ID, layoutResId);
        args.putParcelableArrayList(KEY_VARIANTS, variant);
        args.putString(KEY_PARENT,parent);
        fragmentAddToCart.setArguments(args);
        return fragmentAddToCart;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.layoutResId = args.getInt(KEY_LAYOUT_ID);
        this.productVariants = args.getParcelableArrayList(KEY_VARIANTS);
        this.parent = args.getString(KEY_PARENT);
        currentVariant = 0;
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
            mListener = (OnVarientEvent) context;
        } catch (ClassCastException ex) {

        }
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_variants, container, false);
/*
        rvVariants = (RecyclerView) rootView.findViewById(R.id.rv_variant);
        //ArrayList<Stock> varList = productVariants.get(currentVariant).getVariantValues();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        rvVariants.setLayoutManager(layoutManager);
        adapterRecyclerView = new AdapterRecyclerView(getContext(), layoutResId, productVariants, this);
        rvVariants.setAdapter(adapterRecyclerView);

        tvSelect = (TextView) rootView.findViewById(R.id.tv_select_variant);
        tvSelect.setText(getContext().getString(R.string.select) + " " + parent);
        ImageView imClose = (ImageView) rootView.findViewById(R.id.im_close);
        imClose.setOnClickListener(this);

        imBack = (ImageView) rootView.findViewById(R.id.im_back);
        imBack.setOnClickListener(this);
        if (currentVariant > 0)
            imBack.setVisibility(View.VISIBLE);

*/

        return rootView;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_close:
                if (mListener != null)
                    mListener.onVariantClose();
                break;
            case R.id.im_back:

                break;
        }
    }


    @Override
    public void onItemAdapterClick(Object object, int position, View view) {
        //  ProductVariant productVariant = ProductVariant.class.cast(object);


        if (mListener != null) {
            if (currentVariant < productVariants.size()) {
                mListener.onVariantSelected(object.toString(), currentVariant);
                currentVariant++;
                imBack.setVisibility(View.VISIBLE);
                if (currentVariant == productVariants.size()) {
                    mListener.onVariantCompleted();
                } else {
                    tvSelect.setText(parent);
                }
            }

        }
    }

    @Override
    public void onCartItemDelted(Product product,int layoutPosition) {

    }

    @Override
    public void onCartItemAdded(Product product) {

    }

    /*private void back() {
        if (currentVariant > 0) {
            currentVariant--;
            Stock productVariant = productVariants.get(currentVariant);
            tvSelect.setText();
            updateListUi(productVariant);
            if (currentVariant == 0)
                imBack.setVisibility(View.INVISIBLE);
        }

    }*/

/*
    private void updateListUi(Stock variant) {
        adapterRecyclerView.updateList(productVariants);
        adapterRecyclerView.notifyDataSetChanged();
    }
*/


    public interface OnVarientEvent {
        public void onVariantClose();
        public void onVariantSelected(String selectValue, int position);
        public void onVariantCompleted();
    }
}
