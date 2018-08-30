package com.flashsales.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.flashsales.R;
import com.flashsales.adapters.AdapterSpinner;
import com.flashsales.datamodel.FlashSaleTimer;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductVariant;
import com.flashsales.datamodel.Stock;

public class FragmentVarientSelection extends Fragment implements View.OnClickListener {
    private OnSelectionEvent mListener;
    public Product product;
    private final static String KEY_PRODUCT = "product";
    private RelativeLayout layoutVars;
    private LinearLayout layoutAddToCart;
    private EditText etAmount;
    private int amount = 0;
    private ImageView im, imAdd, imMinus;
    private Button btnCheckout;
    private String valueSelected = "";
    private int position = 0;
    private String parentVarient = "";


    public static FragmentVarientSelection newInstance(Product product) {
        FragmentVarientSelection fragment = new FragmentVarientSelection();
        Bundle args = new Bundle();
        args.putParcelable(KEY_PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        product = args.getParcelable(KEY_PRODUCT);
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
            mListener = (OnSelectionEvent) context;
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_var_selection, container, false);

        layoutVars = (RelativeLayout) view.findViewById(R.id.layout_vars);
        layoutAddToCart = (LinearLayout) view.findViewById(R.id.layout_addtocart);

        im = (ImageView) view.findViewById(R.id.im_product);
        ImageView imClose = (ImageView) view.findViewById(R.id.im_close);

        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvTimer = (TextView) view.findViewById(R.id.tv_timer);
        TextView tvPrice = (TextView) view.findViewById(R.id.tv_price);
        TextView tvBrand = (TextView) view.findViewById(R.id.tv_brand);
        imAdd = (ImageView) view.findViewById(R.id.iv_add);
        imMinus = (ImageView) view.findViewById(R.id.iv_minus);
        etAmount = (EditText) view.findViewById(R.id.et_amount);
        btnCheckout = (Button) view.findViewById(R.id.btn_addtocart);
        btnCheckout.setOnClickListener(this);


        tvBrand.setText(product.getBrand());
        Picasso.with(getContext()).load(product.getImagePaths().get(0)).into(im);
        tvName.setText(product.getName());
        String finalPrice = getContext().getString(R.string.currency);
        finalPrice += " " + product.getPrice();
        tvPrice.setText(finalPrice);
        imAdd.setOnClickListener(this);
        imMinus.setOnClickListener(this);
        imClose.setOnClickListener(this);

        FlashSaleTimer flashSaleTimer = new FlashSaleTimer();
        flashSaleTimer.updateTimerMidnight(tvTimer);

        checkVariants();

        return view;
    }

    private void checkVariants() {
        if (product.getProductVariants() == null)
            return;

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < product.getProductVariants().size(); i++) {
            //   removeViews();
            ProductVariant variant = product.getProductVariants().get(i);
            parentVarient = variant.getVariantParent();
            String select = getContext().getString(R.string.select);
            select += " " + parentVarient;
            list.add(select);

            ArrayList<Stock> stockList = variant.getVariantValues();
            for (int ii = 0; ii < stockList.size(); ii++) {
                Stock stock = stockList.get(ii);
                if (stock.getStock() > 0) {
                    list.add(stock.getValue());
                }
            }
            createSpinner(list);
            //addViews();
        }
    }

    @SuppressLint("ResourceType")
    private void createSpinner(final ArrayList<String> items) {
        Spinner spinner = new Spinner(getContext());
        spinner.setId(1);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, layoutAddToCart.getId());


        RelativeLayout.LayoutParams paramsBtn = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsBtn.addRule(RelativeLayout.BELOW, spinner.getId());
        btnCheckout.setLayoutParams(paramsBtn);
        AdapterSpinner adapter = new AdapterSpinner(getContext(), items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positionA, long id) {
                position = positionA;
                valueSelected = items.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RelativeLayout.LayoutParams paramsLayoutVar = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutVars.setLayoutParams(paramsLayoutVar);
        layoutVars.addView(spinner, params);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                add();
                break;

            case R.id.iv_minus:
                minus();
                break;

            case R.id.btn_addtocart:
                if (mListener != null && checkCount() && checkValue()) {
                    btnCheckout.setText(R.string.adding_to_cart);
                    btnCheckout.setClickable(false);
                    mListener.onAddToCart(Integer.valueOf(etAmount.getText().toString()), valueSelected);
                   /* mListener.onVarientsClose();*/
                }
                if (!checkCount()) {
                    notifyUser(getString(R.string.select_amount));
                }
                if (!checkValue()) {
                    String message = getString(R.string.select_varient);
                    notifyUser(message + " " + product.getParentVarient());
                }
                break;

            case R.id.im_close:
                if (mListener != null)
                    mListener.onVarientsClose();
                break;
        }
    }

    private void add() {
        amount++;
        etAmount.setText(amount + "");
    }

    private void minus() {
        if (amount == 0)
            return;
        amount--;
        etAmount.setText(amount + "");
    }

    private boolean checkCount() {
        return Integer.valueOf(etAmount.getText().toString()) > 0;
    }

    private boolean checkValue() {
        if (product.getProductVariants() == null)
            return true;
        return !valueSelected.equals("") && position != 0;
    }

    private void notifyUser(String message) {
        Snackbar bar = Snackbar.make(layoutVars, message, Snackbar.LENGTH_SHORT);
        bar.show();
    }


    public interface OnSelectionEvent {
        public void onVarientsClose();
        public void onAddToCart(int count,String valueSelected);
    }
}
