package com.flashsales.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.flashsales.R;

public class AdapterInfoTab extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    private String[] titles = new String[]{"Description","Shipping Information"};
    private String []productInto;
    private String [] contents;
    private int mCurrentPosition;
    private View mCurrentView;

    public AdapterInfoTab(Context context,String [] contentList) {
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.productInto = context.getResources().getStringArray(R.array.vp_titles);
        this.contents =contentList;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.item_product_info, container, false);
        view.setTag(position);

        TextView tv = (TextView)view.findViewById(R.id.tv_info);
        tv.setText(contents[position]);
        container.addView(view);

        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentView = (View) object;
    }

    public View getCurrentItem() {
        return mCurrentView;
    }
}
