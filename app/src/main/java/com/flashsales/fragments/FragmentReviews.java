package com.flashsales.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import java.util.ArrayList;

import com.flashsales.MyApplication;
import com.flashsales.R;
import com.flashsales.adapters.AdapterRecyclerView;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.Review;
import com.flashsales.datamodel.FlashSaleTimer;

public class FragmentReviews extends Fragment implements  AdapterRecyclerView.ClickListener {

    private final static String KEY_LIST = "keyList";
    private final static String KEY_RATING= "keyRating";
    private ArrayList<Review> reviewArrayList;
    private double rating;
    private FlashSaleTimer timer ;
    private MyApplication myApplication;

    public static FragmentReviews newInstance(ArrayList<Review>reviews,double rating){
        FragmentReviews fragmentReviews =  new FragmentReviews();
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_LIST,reviews);
        args.putDouble(KEY_RATING,rating);
        fragmentReviews.setArguments(args);
        return fragmentReviews;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  myApplication = (MyApplication) getActivity().getApplicationContext();
       // reviewArrayList = myApplication.getReviews();
        Bundle args = getArguments();
        reviewArrayList = args.getParcelableArrayList(KEY_LIST);
        rating = args.getDouble(KEY_RATING);
        timer = new FlashSaleTimer();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews,container,false);

        RecyclerView rv = (RecyclerView)view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), LinearLayoutManager.VERTICAL));
        LinearLayoutManager lm = new LinearLayoutManager(getActivity().getBaseContext());
        rv.setLayoutManager(lm);
        rv.setAdapter(new AdapterRecyclerView(getActivity().getBaseContext(),R.layout.item_review,reviewArrayList,this));

        RatingBar rb= (RatingBar)view.findViewById(R.id.rb);
        float ratingFlaot  = Float.valueOf(String.valueOf(rating));
        rb.setRating(ratingFlaot);

      /*  TextView tvTimer = (TextView) view.findViewById(R.id.tv_timer);
        timer.updateTimerMidnight(tvTimer);*/

        return view;
    }


    @Override
    public void onItemAdapterClick(Object object, int position, View view) {

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
}
