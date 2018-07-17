package com.flashsales;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.flashsales.Utils.SharedPreferenceUtils;


public class FaqActivity extends AppCompatActivity {

    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        SharedPreferenceUtils prefs = new SharedPreferenceUtils(this);
        count = prefs.getCartCount();
        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.mi_cart);
        menuItem.setIcon(ConverterCartBadge.convertLayoutToImage(this, count, R.drawable.ic_shopping_cart));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_cart:
                loadCart();
                break;
            case android.R.id.home:
                /*onBackPressed();*/
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextPanel tvPanelHow = (TextPanel) findViewById(R.id.panel_how_does_this_work);
        tvPanelHow.setPanelTitle(getString(R.string.how_does_this_work));
        tvPanelHow.setContent(getString(R.string.how_does_this_work_content));

        TextPanel tvPanelMissDeal = (TextPanel) findViewById(R.id.panel_missed_deal);
        tvPanelMissDeal.setPanelTitle(getString(R.string.missed_deal));
        tvPanelMissDeal.setContent(getString(R.string.missed_deal_content));

        TextPanel tvPanelQuestion = (TextPanel) findViewById(R.id.panel_question_product);
        tvPanelQuestion.setPanelTitle(getString(R.string.question_product));
        tvPanelQuestion.setContent(getString(R.string.question_product_content));

        TextPanel tvPanelSupport = (TextPanel) findViewById(R.id.panel_support);
        tvPanelSupport.setPanelTitle(getString(R.string.customer_support));
        tvPanelSupport.setContent(getString(R.string.customer_support_content));

        TextPanel tvPanelProductNotWork = (TextPanel) findViewById(R.id.panel_product_not_work);
        tvPanelProductNotWork.setPanelTitle(getString(R.string.product_not_work));
        tvPanelProductNotWork.setContent(getString(R.string.product_not_work_content));

        TextPanel tvPanelHowRefund = (TextPanel) findViewById(R.id.panel_how_to_return);
        tvPanelHowRefund.setPanelTitle(getString(R.string.how_can_refund));
        tvPanelHowRefund.setContent(getString(R.string.how_can_refund_content));

        TextPanel tvPanelDamagedArrival = (TextPanel) findViewById(R.id.panel_damaged_arrival);
        tvPanelDamagedArrival.setPanelTitle(getString(R.string.damaged_arrivale));
        tvPanelDamagedArrival.setContent(getString(R.string.damaged_arrivale_content));

        TextPanel tvPanelWarranty = (TextPanel) findViewById(R.id.panel_warranty);
        tvPanelWarranty.setPanelTitle(getString(R.string.warranty));
        tvPanelWarranty.setContent(getString(R.string.warranty_content));

        TextPanel tvPanelShipping = (TextPanel) findViewById(R.id.panel_shipping);
        tvPanelShipping.setPanelTitle(getString(R.string.shipping_how_long));
        tvPanelShipping.setContent(getString(R.string.shipping_how_long_content));


    }

    private void loadCart() {
        Intent intent = new Intent(FaqActivity.this, CartActivity.class);
        startActivity(intent);
        finish();
    }
}
