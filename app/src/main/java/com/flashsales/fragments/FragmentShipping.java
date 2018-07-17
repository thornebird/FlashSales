package com.flashsales.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
    // private String fullName;
    public FragmentShipping() {
    }

    public static FragmentShipping newInstance() {
        FragmentShipping fragmentShipping = new FragmentShipping();
        Bundle args = new Bundle();
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
        Button btnSave = (Button) rootView.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

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
            case R.id.btn_save:
                if (checkAllCompleted(editTexts) && mListener != null) {
                    mListener.onAddressSave(shippingAddress());
                }
                break;
        }
    }

    private void initAddress(){
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
