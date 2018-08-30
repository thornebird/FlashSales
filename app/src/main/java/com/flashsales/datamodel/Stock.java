package com.flashsales.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class Stock implements Parcelable {
    private String value;
    private int stock;

    public Stock(){}

    public Stock(String value, int stock) {
        this.value =value;
        this.stock =stock;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Stock(Parcel in) {
        this.value = in.readString();
        this.stock = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Stock createFromParcel(Parcel in) {
            return new Stock(in);
        }

        public Stock[] newArray(int size) {
            return new Stock[size];
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
        dest.writeString(this.value);
        dest.writeInt(this.stock);

    }
}
