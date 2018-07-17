package com.flashsales.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.Review;
import com.flashsales.fragments.FragmentProductProfile;
import com.flashsales.fragments.FragmentReviews;

public class AdapterFragmentPager extends FragmentPagerAdapter {


    private ArrayList<Review> arrayList;
    private String[] titles = new String[]{"Product", "Reviews"};
    private Product product;


    public AdapterFragmentPager(Product product, FragmentManager fragmentManager,ArrayList<Review> reviews) {
        super(fragmentManager);
        this.product = product;
        this.arrayList = reviews;
    }




    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragmentProductProfile.newInstance(product);
            case 1:
                return FragmentReviews.newInstance(arrayList);
            default:
                return null;
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
