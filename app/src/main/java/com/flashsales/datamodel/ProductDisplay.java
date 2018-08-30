package com.flashsales.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ProductDisplay implements Parcelable{
    @SerializedName("name")
    private String name;
    @SerializedName("brand")
    private String brand;
    @SerializedName("shortName")
    private String shortName;
    @SerializedName("image")
    private String image;
    @SerializedName("price")
    private double price;
    @SerializedName("retailPrice")
    private double retailPrice;
    private double fakeRating;
    private boolean isFree;
    public ProductDisplay(){}

    public ProductDisplay(String name,String brand,String shortName,String image,double price,double retailPrice){
        this.name = name;
        this.brand = brand;
        this.shortName = shortName;
        this.image = image;
        this.price = price;
        this.retailPrice = retailPrice;
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public double getFakeRating() {
        return fakeRating;
    }
    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public void setFakeRating(double fakeRating) {
        this.fakeRating = fakeRating;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new ProductDisplay(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new ProductDisplay[size];
        }
    };

    public ProductDisplay(Parcel source){
        this.name =source.readString();
        this.brand =source.readString();
        this.shortName = source.readString();
        this.image = source.readString();
        this.price = source.readDouble();
        this.retailPrice =source.readDouble();
        this.fakeRating = source.readDouble();
        isFree = source.readByte() != 0;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
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
        dest.writeString(this.name);
        dest.writeString(this.brand);
        dest.writeString(this.shortName);
        dest.writeString(this.image);
        dest.writeDouble(this.price);
        dest.writeDouble(this.retailPrice);
        dest.writeDouble(this.fakeRating);
        dest.writeByte((byte) (isFree ? 1 : 0));
    }
}
