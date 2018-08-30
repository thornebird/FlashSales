package com.flashsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.flashsales.datamodel.Product;
import com.flashsales.fragments.FragmentSavedProducts;

import java.util.ArrayList;

public class FragmentPagerSavedProducts extends FragmentPagerAdapter {


    private String[] titles;
    private ArrayList<Product> productsViewed,productsFavourite;
    private int state;


    public FragmentPagerSavedProducts(FragmentManager manager, ArrayList<Product> productsViewed, ArrayList<Product>favouriteProducts) {
        super(manager);
        this.state = state;
        this.productsViewed = productsViewed;
        this.productsFavourite = favouriteProducts;
        this.titles = new String[]{"Viewed Products","Favourite Products"};
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return FragmentSavedProducts.newInstance(productsViewed,0);
            case 1:
                return FragmentSavedProducts.newInstance(productsFavourite,0);
            default:
                return null;
        }
    }
}
