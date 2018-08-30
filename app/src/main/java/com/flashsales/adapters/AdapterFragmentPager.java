package com.flashsales.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import com.flashsales.R;
import com.flashsales.datamodel.OrderObject;
import com.flashsales.datamodel.PrizeOrderObject;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.Review;
import com.flashsales.datamodel.User;
import com.flashsales.fragments.FragmentAccountEdit;
import com.flashsales.fragments.FragmentEmptyCart;
import com.flashsales.fragments.FragmentOrderHistory;
import com.flashsales.fragments.FragmentProductProfile;
import com.flashsales.fragments.FragmentReviews;
import com.flashsales.fragments.FragmentSavedProducts;

public class AdapterFragmentPager extends FragmentStatePagerAdapter {

    private ArrayList<Review> arrayList;
    private String[] titles;
    private Product product;
    private ArrayList<Product> productsViewed, productsFavourite;
    private ArrayList<OrderObject>orders;
    private ArrayList<PrizeOrderObject> prizeOrderObjects;
    private User user;
    private int state;
    public final static int STATE_MAIN_ACTIVITY = 0;
    public final static int STATE_HISTORY_ACTIVITY = 1;
    private FragmentSavedProducts fragmentViewed, fragmentFavourits;
    private Context context;
    private FragmentManager fragmentManager;
    private FragmentEmptyCart fragmentEmptyCart;
    private boolean isFree= false;

    public AdapterFragmentPager(Product product, int state, FragmentManager fragmentManager,boolean isFree ,ArrayList<Review> reviews) {
        super(fragmentManager);
        this.product = product;
        this.arrayList = reviews;
        this.state = state;
        this.isFree = isFree;
        this.titles = new String[]{"Product", "Reviews"};
    }

    public AdapterFragmentPager(Context context, FragmentManager manager, int state,
                                ArrayList<Product> productsViewed,
                                ArrayList<Product> favouriteProducts,ArrayList<OrderObject>orders,ArrayList<PrizeOrderObject>prizeOrders,
                                User user) {
        super(manager);
        this.fragmentManager = manager;
        this.context = context;
        this.state = state;
        this.productsViewed = productsViewed;
        this.productsFavourite = favouriteProducts;
        this.orders = orders;
        this.prizeOrderObjects = prizeOrders;
        this.user = user;
        this.titles = new String[]{ "Viewed", "Favourites","Orders","Account"};
    }

    @Override
    public Fragment getItem(int position) {
        if (state == STATE_MAIN_ACTIVITY) {
            switch (position) {
                case 0:
                    return FragmentProductProfile.newInstance(product,isFree);
                case 1:
                    return FragmentReviews.newInstance(arrayList,product.getFakeRating());


                default:
                    return null;
            }
        } else if (state == STATE_HISTORY_ACTIVITY) {
            switch (position) {


                case 0:
                    if (productsViewed == null || productsViewed.size() == 0) {
                        fragmentEmptyCart = FragmentEmptyCart.newInstance(context.getString(R.string.your_viewed_is_empty));
                        return fragmentEmptyCart;
                    }
                    fragmentViewed = FragmentSavedProducts.newInstance(productsViewed, FragmentSavedProducts.STATE_VIWIED);
                    return fragmentViewed;
                case 1:
                    if (productsFavourite == null || productsFavourite.size() == 0) {
                        fragmentEmptyCart = FragmentEmptyCart.newInstance(context.getString(R.string.your_favourite_is_empty));
                        return fragmentEmptyCart;
                    }
                    fragmentFavourits = FragmentSavedProducts.newInstance(productsFavourite, FragmentSavedProducts.STATE_FAVOURITES);
                    return fragmentFavourits;

                case 2:
                    if(orders == null || orders.size() == 0 ){
                        return  fragmentEmptyCart = FragmentEmptyCart.newInstance(context.getString(R.string.you_have_no_order_history));
                    }
                    FragmentOrderHistory fragmentOrderHistory = FragmentOrderHistory.newInstance(orders,prizeOrderObjects);
                    return fragmentOrderHistory;

                /*case 3:
                    if(prizeOrderObjects == null || prizeOrderObjects.size()==0){
                        return  fragmentEmptyCart = FragmentEmptyCart.newInstance(context.getString(R.string.you_have_no_orderprizes_history));
                      }

                      return  fragmentEmptyCart = FragmentEmptyCart.newInstance(context.getString(R.string.you_have_no_orderprizes_history));
              */  //fragmetn prize order lsit
                case 3:
                    return FragmentAccountEdit.newInstance(user);
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
