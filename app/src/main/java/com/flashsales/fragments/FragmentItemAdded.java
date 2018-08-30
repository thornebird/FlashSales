package com.flashsales.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flashsales.R;
import com.flashsales.datamodel.Product;
import com.squareup.picasso.Picasso;

public class FragmentItemAdded extends DialogFragment {

    private Product product;
    private final static String KEY_PRODUCT = "product";
    public FragmentItemAdded(){}

    public static FragmentItemAdded newInstance(Product product){
        FragmentItemAdded fragmentItemAdded =  new FragmentItemAdded();
        Bundle  args  = new Bundle();
        args.putParcelable(KEY_PRODUCT,product);
        fragmentItemAdded.setArguments(args);
        return fragmentItemAdded;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        product = args.getParcelable(KEY_PRODUCT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_item_added,container,false);

        ImageView imProduct = (ImageView) rootView.findViewById(R.id.im_product);
        TextView tvProduct = (TextView)rootView.findViewById(R.id.tv_item_added);
        TextView tvSaved = (TextView)rootView.findViewById(R.id.tv_saved);

        Picasso.with(getContext()).load(product.getImages().get(0)).into(imProduct);

        tvProduct.setText(product.getName());

        int discount = Integer.parseInt(product.getRetailPrice()) - Integer.parseInt(product.getPrice());
        tvSaved.setText(getContext().getString(R.string.you_saved)+" "+discount);
        return rootView;
    }



}
