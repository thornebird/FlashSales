package com.flashsales.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CartProduct implements Parcelable {
    @SerializedName("token")
    private String token;
    @SerializedName("expiresOn")
    private String expiresOn;
    @SerializedName("products")
    private ArrayList<Product> products;
    private String expiresInMinutes;
    private int salePrice;
    private int retailPrice;

    public CartProduct() {
    }

    public CartProduct(String token, String expiresOn) {
        this.expiresOn = expiresOn;
        this.token = token;
        setExpiresOn(expiresOn);
        setTotalPrice();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(String expiresOn) {
        this.expiresOn = expiresOn;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }


    public String getExpiresInMinutes() {
        return expiresInMinutes;
    }

    public void setTotalPrice(){
        salePrice = 0;
        retailPrice = 0;
        if(products == null)
            return;
        for(int i=0;i<products.size();i++){
            int price = Integer.valueOf(products.get(i).getPrice());
            salePrice += price*products.get(i).getStock();
            int retail = Integer.valueOf(products.get(i).getRetailPrice());
            this.retailPrice +=  retail*products.get(i).getStock();
        }
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public int getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(int retailPrice) {
        this.retailPrice = retailPrice;
    }

    public static Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public CartProduct createFromParcel(Parcel in) {
            return new CartProduct(in);
        }

        public CartProduct[] newArray(int size) {
            return new CartProduct[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.expiresOn);
        dest.writeString(this.token);
        dest.writeList(this.products);
        dest.writeString(this.expiresInMinutes);
    }

    public CartProduct(Parcel in) {
        this.token = in.readString();
        this.expiresOn = in.readString();
        this.products = in.readArrayList(null);
        this.expiresInMinutes = in.readString();
    }
}
