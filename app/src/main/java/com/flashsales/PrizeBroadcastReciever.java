package com.flashsales;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.flashsales.Utils.Configs;
import com.flashsales.dao.DBFavouriteProducts;
import com.flashsales.dao.DBViewedProducts;
import com.flashsales.dao.ProductApi;
import com.flashsales.datamodel.CartProduct;
import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;

import java.util.ArrayList;

public class PrizeBroadcastReciever extends BroadcastReceiver implements ProductApi.OnLoaded {

    private int amountFilter;
    private ProductApi productApi;
    private Context mContext;
   /* private  Product freeProduct;
    private ProductDisplay  freeProductDisplay;*/
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        productApi = new ProductApi();
        productApi.setListener(this);
        productApi.viewCart(context);
    }

    public void findProduct() {
        DBViewedProducts dbViewedProducts = new DBViewedProducts(mContext);
        ArrayList<Product> viewedProducts = dbViewedProducts.getViewedProducts();

        DBFavouriteProducts dbFavouriteProducts = new DBFavouriteProducts(mContext);
        ArrayList<Product> favouriteProducts = dbFavouriteProducts.getFavouriteProducts();
        Product freeProduct = new Product();
        if (favouriteProducts.size() > 0) {
           freeProduct = verifyPrice(favouriteProducts);
            if (freeProduct.getPrice() != null) {
                ///set views
                //  setViews(freeProduct.getName(), freeProduct.getPrice(), freeProduct.getImage());
                notification(freeProduct);
            }

        } else {
           freeProduct = verifyPrice(viewedProducts);
            if (freeProduct.getPrice() != null) {
                ///set views
                notification(freeProduct);
            }

        }
        if (freeProduct.getPrice() == null) {
            if (productApi == null)
                productApi = ProductApi.getInstance();
            productApi.setListener(this);
            productApi.okHttpRequestProducts(mContext);

        }

    }

    public Product verifyPrice(ArrayList<Product> list) {
        amountFilter = 15;

        Product product = new Product();
        for (int i = 0; i < list.size(); i++) {
            if (Integer.valueOf(list.get(i).getPrice()) <= amountFilter) {
                product = list.get(i);
                return product;
            }
        }
        return product;
    }

    private ProductDisplay verifyProductDisplay(ArrayList<ProductDisplay> list) {
        amountFilter = 15;
        ProductDisplay productDisplay = new ProductDisplay();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPrice() <= amountFilter) {
                productDisplay = list.get(i);
                return productDisplay;
            }
        }
        return productDisplay;
    }

    private void notification(Object product){
        ProductDisplay sendProduct;
        if(product instanceof Product){
            Product productDemo = (Product) product;
            sendProduct = new ProductDisplay();
            sendProduct.setShortName(productDemo.getShortName());
            sendProduct.setName(productDemo.getName());
            sendProduct.setBrand(productDemo.getBrand());
            sendProduct.setImage(productDemo.getImage());
            sendProduct.setRetailPrice(Double.valueOf(productDemo.getRetailPrice()));
            sendProduct.setPrice(Double.valueOf(productDemo.getPrice()));
            sendProduct.setFree(true);
        }else{
            sendProduct = (ProductDisplay)product;
            sendProduct.setFree(true);
        }
        Intent intentPrize = new Intent(mContext,ProductActivity.class);
        intentPrize.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intentPrize.putExtra(ProductActivity.KEY_PRODUCT, sendProduct);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,intentPrize,PendingIntent.FLAG_CANCEL_CURRENT);

        String channelID = Configs.KEY_NOTI_CHANEL_NAME;;

        if (BuildConfig.DEBUG) {
            channelID =  Configs.KEY_NOTI_CHANEL_NAME_DEBUG;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelID);

        builder.setSmallIcon(R.drawable.logo)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(mContext.getString(R.string.prize_free))
                .setContentText(mContext.getString(R.string.prize_claim))
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }


    @Override
    public void onProductLoaded(Product productObject) {

    }

    @Override
    public void onProductsLoaded(ArrayList<ProductDisplay> productDisplays) {
         ProductDisplay freeProductDisplay = verifyProductDisplay(productDisplays);
        if (freeProductDisplay != null) {
            notification(freeProductDisplay);
        }
    }

    @Override
    public void onCartAdded(CartProduct cartProduct) {

    }

    @Override
    public void onCartDelted(CartProduct cartProduct) {

    }

    @Override
    public void onCartLoaded(CartProduct cartProduct) {
      Product  freeProduct = verifyPrice(cartProduct.getProducts());
        if (freeProduct.getPrice() == null) {
            findProduct();
        } else {

            notification(freeProduct);
        }
    }

    @Override
    public void onCartLoadedEmpty() {
        findProduct();
    }

    @Override
    public void onCheckoutCartEmptied() {

    }
}
