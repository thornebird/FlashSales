package com.flashsales.dao;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.flashsales.datamodel.Product;
import com.flashsales.datamodel.ProductDisplay;



public class ProductsRequest {
    private OnProductsLoaded mListener;
    private ArrayList<ProductDisplay> productDisplays;
    private final static String queryProductsUrl = "http://ec2-35-176-212-213.eu-west-2.compute.amazonaws.com/products";
    private ArrayList<ProductDisplay> productArrayList;
    private Gson gson;
    private String valuedd;

    public ProductsRequest(OnProductsLoaded mListener) {
        this.mListener = mListener;
    }

    public JsonArrayRequest productsRequest() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, queryProductsUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() > 0)
                            productArrayList = new ArrayList<>();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                // Get current json object
                                JSONObject product = response.getJSONObject(i);

                                String name = product.getString("name");
                                String brand = product.getString("brand");
                                String shortName = product.getString("shortName");
                                String image = product.getString("image");
                                String price = product.getString("price");
                                String retailPrice = product.getString("retailPrice");
                                Log.i("product name", name);
                                Log.i("product brand", brand);
                                double priceDouble = Double.parseDouble(price);
                                double retailDouble = Double.parseDouble(retailPrice);
                                productArrayList.add(new ProductDisplay(name, brand, shortName, image, priceDouble, retailDouble));

                            }
                        } catch (JSONException ex) {

                        }
                        if (mListener != null)
                            mListener.onDisplaysLoaded(productArrayList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        return jsonArrayRequest;
    }

    public StringRequest productRequest(String brand, String name) {

        final String params = "/" + brand + "/" + name;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, queryProductsUrl + params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gson = gsonBuilder.create();


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                           /* int count = 0;
                            Iterator<String> keytsItr = jsonObject.keys();
                            while (keytsItr.hasNext()) {
                                String key = keytsItr.next();
                                Object value = jsonObject.get(key);
                                valuedd = jsonObject.get(key).toString();
                                Log.d("key==", count + " >" + key + ", value:" + value.toString());
                                if (key.equals("variants")) {

                                    String variant = value.toString();


                                    JSONObject jsonObjectVariant = new JSONObject(variant);

                          *//*          ProductFeature productFeature = gson.fromJson(variant,ProductFeature.class);
                                    productFeatures.add(productFeature);*//*


                                }

                            }*/
                            String jsonString = response.toString();
                            JSONObject  jsonObjectVariants = jsonObject.getJSONObject("variants");
                            Product productObject = gson.fromJson(jsonString, Product.class);

                            //Type featuresList = new TypeToken<ArrayList<ProductFeature>>(){}.getType();
                            //   ArrayList <String> features = gson.fromJson(response.toString(),featuresList);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        return stringRequest;
    }



    public interface OnProductsLoaded {
        public void onDisplaysLoaded(ArrayList<ProductDisplay> displayList);

        public void onProductOpened(Product product);
    }
}
