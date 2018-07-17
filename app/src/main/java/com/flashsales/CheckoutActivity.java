package com.flashsales;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import com.flashsales.R;

import com.flashsales.fragments.FragmentShipping;

public class CheckoutActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private FragmentShipping fragmentShipping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        init();
        inflateShipping();
    }

    private void init(){
        frameLayout = (FrameLayout)findViewById(R.id.frame);
    }

    private void inflateShipping(){
        if(fragmentShipping==null)
            fragmentShipping = FragmentShipping.newInstance();
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame,fragmentShipping).commit();

    }
}
