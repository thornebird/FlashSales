package com.flashsales.dao;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.flashsales.CartBroadCastReciever;
import com.flashsales.ProductActivity;
import com.flashsales.TimeBroadcastReciever;
import com.flashsales.Utils.SharedPreferenceUtils;
import com.flashsales.Utils.Utils;
import com.flashsales.datamodel.CartProduct;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;
import com.flashsales.datamodel.ProductVariant;
import com.flashsales.datamodel.Stock;

public class ProductApi {
    private final static String baseUrlProducts = "http://ec2-35-176-212-213.eu-west-2.compute.amazonaws.com/products";
    private final static String baseUrlCartAdd = "http://ec2-35-176-212-213.eu-west-2.compute.amazonaws.com/cart/add";
    private final static String baseUrlCartRemove = "http://ec2-35-176-212-213.eu-west-2.compute.amazonaws.com/cart/remove";
    private final static String baseUrlCartVC = "http://ec2-35-176-212-213.eu-west-2.compute.amazonaws.com/cart/";
    // private Gson gson;
    private OnLoaded mListener;
    private static ProductApi mInstance;
    private Context context;

    public static synchronized ProductApi getInstance() {
        if (mInstance == null) {
            mInstance = new ProductApi();
        }

        return mInstance;
    }

    public void setListener(OnLoaded mListener) {
        this.mListener = mListener;
    }

    public void okhttpRequestProduct(String brand, String name) {
        final String urlFinal = baseUrlProducts + "/" + brand + "/" + name;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlFinal)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    Product productObject = null;
                    String responseStr = response.body().string();
                    ArrayList<ProductVariant> productVariants = new ArrayList<>();
                    try {
                        JSONObject jsonObject = new JSONObject(responseStr);
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        final Gson gson = gsonBuilder.create();

                        productObject = gson.fromJson(responseStr, Product.class);
                        JSONObject jsonObjectVariant = jsonObject.getJSONObject("variants");
                        ArrayList<Stock> valuesMap = new ArrayList<>();
                        int stock = 0;
                        if (jsonObjectVariant.has("color")) {

                            Iterator<String> keyItr = jsonObjectVariant.keys();
                            while (keyItr.hasNext()) {
                                String key = keyItr.next();

                                if (key.equals("color") || key.equals("size")) {
                                    String keyParentVariant = key.toString();
                                    productObject.setParentVarient(keyParentVariant);
                                    JSONArray valuesArray = jsonObjectVariant.getJSONArray(key);
                                    for (int i = 0; i < valuesArray.length(); i++) {
                                        JSONObject jsonObjectValue = valuesArray.getJSONObject(i);
                                        Iterator<String> keyItrVaraitns = jsonObjectValue.keys();

                                        while (keyItrVaraitns.hasNext()) {
                                            String keyVariant = keyItrVaraitns.next();
                                            JSONObject jsonObject1 = jsonObjectValue.getJSONObject(keyVariant);
                                            int stockVariant = jsonObject1.getInt("stock");
                                            valuesMap.add(new Stock(keyVariant, stockVariant));
                                        }
                                    }
                                    productVariants.add(new ProductVariant(key, valuesMap));

                                }
                                if (valuesMap.size() > 0) {
                                    productObject.setProductVariants(productVariants);
                                }

                            }

                        } else {
                            stock = jsonObjectVariant.getInt("stock");
                        }

                        if (valuesMap.size() > 0) {
                            productObject.setProductVariants(productVariants);
                        } else if (stock != 0) {
                            productObject.setStock(stock);
                        }

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    if (mListener != null)
                        mListener.onProductLoaded(productObject);
                } else {
                    // Request not successful
                    Log.e("error", "failed");
                }
            }
        });
    }

    public void okHttpRequestProducts() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.create();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrlProducts)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    ArrayList<ProductDisplay> displayArrayList = gson.fromJson(responseStr, new TypeToken<List<ProductDisplay>>() {
                    }.getType());

                    if (mListener != null)
                        mListener.onProductsLoaded(displayArrayList);

                } else {
                    // Request not successful
                }
            }
        });
    }


    public void addProductCart(final Context context, final Product product) {
        final SharedPreferenceUtils utils = new SharedPreferenceUtils(context);
        JSONObject jsonRequest = new JSONObject();
        JSONObject jsonRequestBody = new JSONObject();
        JSONObject jsonStockbody = new JSONObject();
        JSONObject jsonSelectedObject = new JSONObject();
        JSONObject jsonParentObject = new JSONObject();

        try {
            jsonRequestBody.put("name", product.getName());
            jsonRequestBody.put("brand", product.getBrand());
            jsonRequest.put("token", utils.getCartToken());


            if (product.getProductVariants() == null) {

                jsonStockbody.put("stock", product.getStock());
                jsonRequestBody.put("variants", jsonStockbody);
                jsonRequest.put("product", jsonRequestBody);
            } else {

                jsonStockbody.put("stock", product.getStock());
                jsonSelectedObject.put(product.getVarientSelected(), jsonStockbody);

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonSelectedObject);

                jsonParentObject.put(product.getParentVarient(), jsonArray);
                jsonRequestBody.put("variants", jsonParentObject);
                jsonRequest.put("product", jsonRequestBody);

            }
        } catch (JSONException ex) {
        }


        final OkHttpClient okHttpClient = new OkHttpClient();
        MediaType Json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(Json, jsonRequest.toString());
        final Request request = new Request.Builder()
                .url(baseUrlCartAdd)
                .post(requestBody)
                .build();

        final Gson gson = new Gson();

        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("response failed ", e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    CartProduct cartProduct = gson.fromJson(responseString, CartProduct.class);
                    utils.setCartToken(cartProduct.getToken());
                    utils.setCartCount(cartProduct.getProducts().size());
                    if(!cartProduct.getExpiresOn().equals("")) {
                        utils.setTimeOut(Long.parseLong(cartProduct.getExpiresOn()));
                        setTimeoutAlarm(context,Long.parseLong(cartProduct.getExpiresOn()));
                    }else {
                        deleteTimeout(context);
                    }
                    setVariants( responseString, cartProduct);
                    if (mListener != null && cartProduct != null)
                        mListener.onCartAdded(cartProduct);
                } else {
                }
            }
        });
    }


    public void deleteProductCart(final Context context, Product product) {
        final SharedPreferenceUtils utils = new SharedPreferenceUtils(context);
        JSONObject jsonRequest = new JSONObject();
        JSONObject jsonRequestBody = new JSONObject();
        JSONObject jsonStockbody = new JSONObject();
        JSONObject jsonSelectedObject = new JSONObject();
        JSONObject jsonParentObject = new JSONObject();

        try {
            jsonRequestBody.put("name", product.getName());
            jsonRequestBody.put("brand", product.getBrand());
            jsonRequest.put("token", utils.getCartToken());


            if (product.getProductVariants() == null) {

                jsonStockbody.put("stock", product.getStock());
                jsonRequestBody.put("variants", jsonStockbody);
                jsonRequest.put("product", jsonRequestBody);
            } else {

                jsonStockbody.put("stock", product.getStock());
                jsonSelectedObject.put(product.getVarientSelected(), jsonStockbody);

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonSelectedObject);

                jsonParentObject.put(product.getParentVarient(), jsonArray);
                jsonRequestBody.put("variants", jsonParentObject);
                jsonRequest.put("product", jsonRequestBody);

            }
        } catch (JSONException ex) {
        }


        final OkHttpClient okHttpClient = new OkHttpClient();
        MediaType Json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(Json, jsonRequest.toString());
        final Request request = new Request.Builder()
                .url("http://ec2-35-176-212-213.eu-west-2.compute.amazonaws.com/cart/remove")
                .delete(requestBody)
                .build();

        final Gson gson = new Gson();

        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    CartProduct cartProduct = gson.fromJson(responseString, CartProduct.class);
                    utils.setCartCount(cartProduct.getProducts().size());
                    utils.setCartToken(cartProduct.getToken());
                    if (!cartProduct.getExpiresOn().equals("")) {
                        utils.setTimeOut(Long.parseLong(cartProduct.getExpiresOn()));
                        setTimeoutAlarm(context, Long.parseLong(cartProduct.getExpiresOn()));
                    }else {
                        deleteTimeout(context);
                    }
                    setVariants(responseString, cartProduct);
                    if (mListener != null && cartProduct != null)
                        mListener.onCartDelted(cartProduct);
                    Log.d("responseString ", responseString);
                } else {
                    if (mListener != null)
                        mListener.onCartLoadedEmpty();
                }
            }
        });

    }

    public void viewCart(final Context context) {
        final SharedPreferenceUtils utils = new SharedPreferenceUtils(context);
        final String tokenSaved = utils.getCartToken();
        String url = baseUrlCartVC + tokenSaved;
        final Gson gson = new Gson();

        final OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.i("cart request failed", e.toString());
            }

            @Override
            public void onResponse(Response response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();

                        CartProduct cartProduct = gson.fromJson(responseString, CartProduct.class);
                        utils.setCartToken(cartProduct.getToken());
                        utils.setCartCount(cartProduct.getProducts().size());

                        if(!cartProduct.getExpiresOn().equals("")){
                            utils.setTimeOut(Long.parseLong(cartProduct.getExpiresOn()));
                            setTimeoutAlarm(context,Long.parseLong(cartProduct.getExpiresOn()));
                        }else {
                            deleteTimeout(context);
                        }

                        setVariants(responseString, cartProduct);


                        if (cartProduct.getProducts() == null && mListener != null) {
                            utils.setCartCount(cartProduct.getProducts().size());
                            mListener.onCartLoadedEmpty();
                        }

/*
                        JSONObject jsonResponseObject = new JSONObject(responseString);


                        Log.i("cart responseString", responseString.toString());*/
                        if (mListener != null && cartProduct != null && cartProduct.getProducts() != null)
                            mListener.onCartLoaded(cartProduct);
        /*            ArrayList<CartProduct> displayArrayList = gson.fromJson(responseString, new TypeToken<List<CartProduct>>() {
                    }.getType());
                    */
                    } catch (IOException ex) {
                        Log.i("cart exception", ex.toString());
                    } /*catch (JSONException ex) {

                    }*/
                } else {
                    if (mListener != null)
                        mListener.onCartLoadedEmpty();
                }
            }
        });

    }


    private void setVariants(/*SharedPreferenceUtils utils*/ String responseString, CartProduct cartProduct) {
        try {
            JSONObject jsonObject = new JSONObject(responseString);
            JSONArray jsonArray = jsonObject.getJSONArray("products");
            for (int i = 0; i < jsonArray.length(); i++) {

                Product product = cartProduct.getProducts().get(i);
                JSONObject jsonProduct = jsonArray.getJSONObject(i);
                JSONObject jsonObjectVariant = jsonProduct.getJSONObject("variants");
                if (jsonObjectVariant.has("color")) {
                    JSONArray jsonVariantArray = jsonObjectVariant.getJSONArray("color");
                    int count = 0;
                    for (int ii = 0; ii < jsonVariantArray.length(); ii++) {

                        JSONObject jsonVariant = jsonVariantArray.getJSONObject(ii);
                        Iterator<String> keyItr = jsonVariant.keys();
                        while (keyItr.hasNext()) {
                            String key = keyItr.next();
                            JSONObject jsonObjectValue = jsonVariant.getJSONObject(key);
                            int stock = jsonObjectValue.getInt("stock");
                            count += stock;
                            product.setStock(count);
                        }

                    }

                } else if (!jsonObjectVariant.has("color")) {
                    int stock = jsonObjectVariant.getInt("stock");
                    product.setStock(stock);
                }
                Log.i("cart products", cartProduct.getProducts().toString());

            }

            //utils.setCartToken(cartProduct.getToken());
            // utils.setCartCount(cartProduct.getProducts().size());


        } catch (JSONException ex) {
            Log.e("error json", ex.toString());
        }
    }

    private void setTimeoutAlarm(Context context,long timeout) {
        if(context == null || Long.valueOf(timeout) == null)
            return;

        Intent intent = new Intent(context, TimeBroadcastReciever.class);
        intent.putExtra(Utils.keyTimeout, Utils.reqCodeTimeout);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Utils.reqCodeTimeout, intent, 0);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC_WAKEUP, timeout, pendingIntent);
    }

    private void deleteTimeout(Context context){
        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, TimeBroadcastReciever.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public interface OnLoaded {
        public void onProductLoaded(Product productObject);
        public void onProductsLoaded(ArrayList<ProductDisplay> productDisplays);
        public void onCartAdded(CartProduct cartProduct);
        public void onCartDelted(CartProduct cartProduct);
        public void onCartLoaded(CartProduct cartProduct);
        public void onCartLoadedEmpty();
    }
}
