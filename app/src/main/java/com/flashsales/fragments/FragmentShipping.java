package com.flashsales.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.flashsales.MyApplication;
import com.flashsales.R;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.datamodel.ShippingAddress;


public class FragmentShipping extends Fragment implements View.OnClickListener {
    private OnAddressEvent mListener;
    private TextInputEditText etFullName, etShipping, etApt, etCountry, etProvince, etCity, etPostcode, etPhone;
    private MyApplication myApplication;
    private ArrayList<EditText> editTexts;
    private ShippingAddress address;
    private ConstraintLayout frame;
    private final static String KEY_SHIPPING_PRICE = "keyShippingPrice";
   // private boolean isFreeShipping;
    private double price;

    // private String fullName;
    public FragmentShipping() {
    }

    public static FragmentShipping newInstance(double shippingPrice) {
        FragmentShipping fragmentShipping = new FragmentShipping();
        Bundle args = new Bundle();
        args.putDouble(KEY_SHIPPING_PRICE,shippingPrice);
        fragmentShipping.setArguments(args);
        return fragmentShipping;
    }

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnAddressEvent) context;
        } catch (ClassCastException ex) {

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
       // isFreeShipping = args.getBoolean(KEY_FREE_SHIPPING);
        price = args.getDouble(KEY_SHIPPING_PRICE);
        editTexts = new ArrayList<>();
        SharedPreferenceUtils prefs = new SharedPreferenceUtils(getActivity());
        address = prefs.getAddress();
        //this.fullName = prefs.getUser().getName();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shipping, container, false);
        //etFullName = (EditText) rootView.findViewById(R.id.et_fullname);
        frame = (ConstraintLayout) rootView.findViewById(R.id.frame_content);
        etShipping = (TextInputEditText) rootView.findViewById(R.id.et_shipping);
        etApt = (TextInputEditText) rootView.findViewById(R.id.et_apt);
        etCountry = (TextInputEditText) rootView.findViewById(R.id.et_country);
        etProvince = (TextInputEditText) rootView.findViewById(R.id.et_province);
        etCity = (TextInputEditText) rootView.findViewById(R.id.et_city);
        etPostcode = (TextInputEditText) rootView.findViewById(R.id.et_postcode);
        etPhone = (TextInputEditText) rootView.findViewById(R.id.et_phone);
        editTexts.add(etShipping);
        editTexts.add(etApt);
        editTexts.add(etCountry);
        editTexts.add(etProvince);
        editTexts.add(etCity);
        editTexts.add(etPostcode);
        editTexts.add(etPhone);

        TextView tvShippingPrice = (TextView) rootView.findViewById(R.id.tv_shippig_price);
        TextView tvContinue = (TextView)rootView.findViewById(R.id.tv_continue);

        /*Button btnSave = (Button) rootView.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);*/

        ConstraintLayout layoutContinue = (ConstraintLayout) rootView.findViewById(R.id.layout_continue);
        layoutContinue.setOnClickListener(this);

      //  ConstraintLayout frameDiscount = (ConstraintLayout) rootView.findViewById(R.id.layout_discount_claimed);
        ImageView ivDiscount =  (ImageView)rootView.findViewById(R.id.iv_discount);

        if(price == 0.0){
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            ivDiscount.startAnimation(animation);
            tvShippingPrice.startAnimation(animation);


        }else if (price>0.0){
            tvShippingPrice.setText(getString(R.string.currency)+price+"");
            tvContinue.setText(getString(R.string.confirm_address));
        }

        initAddress();
       /* if(!fullName.equals(""))
            etFullName.setText(fullName);
*/
        return rootView;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_continue:
                if (checkAllCompleted(editTexts) && mListener != null) {
                    mListener.onAddressSave(shippingAddress());
                } else {
                    Snackbar.make(frame, getString(R.string.shipping_fields_empty), Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initAddress() {
        etShipping.setText(address.getShippingAddress());
        etApt.setText(address.getApt());
        etCity.setText(address.getCity());
        etPostcode.setText(address.getZipCode());
        etProvince.setText(address.getProvince());
        etCountry.setText(address.getCountry());
        etPhone.setText(address.getTelephone());
    }

    private ShippingAddress shippingAddress() {

        ShippingAddress address = new ShippingAddress();
        address.setTelephone(etPhone.getText().toString());
        address.setShippingAddress(etShipping.getText().toString());
        address.setApt(etApt.getText().toString());
        address.setCity(etCity.getText().toString());
        address.setProvince(etProvince.getText().toString());
        address.setCountry(etCountry.getText().toString());
        address.setZipCode(etPostcode.getText().toString());
        return address;
    }

    private boolean checkAllCompleted(ArrayList<EditText> editTexts) {
        boolean isFilled = true;
        for (int i = 0; i < editTexts.size(); i++) {
            Log.d("log", editTexts.get(i).toString());
            if (editTexts.get(i).getText().length() < 1) {
                isFilled = false;
                editTexts.get(i).setError(getString(R.string.field_required));
            }
        }

        return isFilled;
    }

    public interface OnAddressEvent {
        public void onAddressSave(ShippingAddress shippingAddress);
    }
}
