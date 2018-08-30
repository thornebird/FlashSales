package com.flashsales.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.flashsales.R;


public class AdapterImageSlider extends PagerAdapter {

    private OnImageClick mlistener;
    private Context context;
    private LayoutInflater inflater;
    private List<String> images;

    public AdapterImageSlider(Context context,List<String>images, OnImageClick mlistener) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.images = images;
        this.mlistener = mlistener;
    }

    @Override
    public int getCount() {
        return images.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.image_product, container, false);

        ImageView imProduct = (ImageView) view.findViewById(R.id.im_product);
        imProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mlistener != null){
                    mlistener.onImageClick();
                }
            }
        });
        Picasso.with(context).load(images.get(position)).placeholder(R.drawable.logo).into(imProduct);
        container.addView(view);

        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

    public interface OnImageClick{
        public void onImageClick();
    }
}
