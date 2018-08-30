package com.flashsales;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.fragments.FragmentShipping;

public class CheckoutActivity extends AppCompatActivity implements NetworkChangeReciever.NetworkListener {

    private FrameLayout frameLayout;
    private FragmentShipping fragmentShipping;
    private ErrorAlert errorAlert;
    private MyApplication myApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        myApplication = MyApplication.getInstance();
        myApplication.setListenerNetwork(this);
        init();
        inflateShipping();
    }

    private void init(){
        frameLayout = (FrameLayout)findViewById(R.id.frame_content);
    }

    private void inflateShipping(){
        if(fragmentShipping==null)
            fragmentShipping = FragmentShipping.newInstance(0.0);
        if(!isFinishing()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frame_content, fragmentShipping).commit();
        }

    }




    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(myApplication!=null)
            myApplication.setListenerNetwork(this);
    }

    @Override
    public void networkAvailable() {
        hideInternetError();
    }

    @Override
    public void networkUnavailable() {
        showInternetError();

    }

    private void showInternetError(){
        errorAlert = new ErrorAlert(this);
    }

    private void hideInternetError(){
        if(errorAlert!=null)
            errorAlert.stopDialog();
    }
}
