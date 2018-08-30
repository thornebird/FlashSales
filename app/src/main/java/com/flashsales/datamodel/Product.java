package com.flashsales.datamodel;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Product implements Parcelable {
    @SerializedName("name")
    private String name;
    @SerializedName("price")
    private String price;
    @SerializedName("brand")
    private String brand;
    @SerializedName("retailPrice")
    private String retailPrice;
    @SerializedName("description")
    private String description;
    @SerializedName("shippingPrice")
    private String shippingPrice;
    @SerializedName("features")
    private List<String> features;
    @SerializedName("images")
    private List<String> images;
    @SerializedName("shortName")
    private String shortName;
    @SerializedName("image")
    private String image;
    private int count;
    private String discount;
    private double fakeRating;


    private ArrayList<ProductVariant> productVariants;
    private String varientSelected;
    private String parentVarient;



    public Product() {
    }

    public Product(String id, String name, String brand, String discount, String price, double rating, int orderCount) {
        this.name = name;
        this.brand = brand;
        this.discount = discount;
        this.price = price;
        this.fakeRating = rating;
       // this.cartCount = orderCount;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDiscount() {
        return discount;
    }



    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getFakeRating() {
        return fakeRating;
    }

    public void setFakeRating(double fakeRating) {
        this.fakeRating = fakeRating;
    }

  /*  //public int getCartCount() {
        return cartCount;
    }*/
/*
    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }*/

    public List<String> getImagePaths() {
        return images;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.images = imagePaths;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

/*  public void setVariantsArrayList(ArrayList<Stock> variantsArrayList) {
        this.variantsArrayList = variantsArrayList;
    }*/

    public String getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
    }



    public ArrayList<ProductVariant> getProductVariants() {
        return productVariants;
    }

    public void setProductVariants(ArrayList<ProductVariant> productVariants) {
        this.productVariants = productVariants;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(String shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getVarientSelected() {
        return varientSelected;
    }

    public void setVarientSelected(String varientSelected) {
        this.varientSelected = varientSelected;
    }

/*
    public ArrayList<Stock> getStock() {
        return stockArrayList;
    }

    public void setStock(ArrayList<Stock> stockArrayList) {
        this.stockArrayList = stockArrayList;
    }
*/

    public int getStock() {
        return count;
    }

    public void setStock(int stock) {
        this.count = stock;
    }

    public String getParentVarient() {
        return parentVarient;
    }

    public void setParentVarient(String parentVarient) {
        this.parentVarient = parentVarient;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.brand);
        dest.writeString(this.discount);
        dest.writeString(this.price);
        dest.writeString(this.retailPrice);
        dest.writeString(this.shippingPrice);
        dest.writeDouble(this.fakeRating);
        dest.writeList(this.images);
        dest.writeString(this.shortName);
        dest.writeString(this.image);
        dest.writeString(this.varientSelected);
        dest.writeString(this.parentVarient);
        dest.writeInt(this.count);
        dest.writeList(this.productVariants);
    }

    public Product(Parcel in) {
        this.name = in.readString();
        this.brand = in.readString();
        this.discount = in.readString();
        this.price = in.readString();
        this.retailPrice = in.readString();
        this.shippingPrice = in.readString();
        this.fakeRating = in.readDouble();
        this.images = in.readArrayList(null);
        this.shortName = in.readString();
        this.image = in.readString();
        this.varientSelected = in.readString();
        this.parentVarient = in.readString();
        this.count =  in.readInt();
        this.productVariants = in.readArrayList(Product.class.getClassLoader());

    }
}
