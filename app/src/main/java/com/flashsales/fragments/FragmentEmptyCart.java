package com.flashsales.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.flashsales.MainActivity;
import com.flashsales.R;

public class FragmentEmptyCart extends Fragment implements View.OnClickListener {

    private String emptyMessage;
    private final static String KEY_MESSAGE ="keyMessage";
    private final static String KEY_ACTIVITY ="keyActivity";
 /*   private int currentActivity;
    public final static int    CART_ACTIVITY=0;
    public final static int  HISTORY_ACTIVITY =1;*/

    public static FragmentEmptyCart newInstance(String emptyMessage){
        FragmentEmptyCart fragment = new FragmentEmptyCart();
        Bundle args = new Bundle();
        args.putString(KEY_MESSAGE,emptyMessage);
        fragment.setArguments(args);
        return fragment;
    }

    /*public void setActivity(Activity activity){
        this.mActivity = activity;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        emptyMessage = args.getString(KEY_MESSAGE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_emptycart,container,false);

        TextView tv= (TextView)rootView.findViewById(R.id.tv_empty);
        tv.setText(emptyMessage);

        Button btnContinue =(Button)rootView.findViewById(R.id.btn_continue_shopping);
        btnContinue.setOnClickListener(this);
        return rootView;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Log.i("emptyc","clicked");
        Intent intent = new Intent(getActivity(), MainActivity.class);;
        startActivity(intent);
        getActivity().finish();
    }
}
