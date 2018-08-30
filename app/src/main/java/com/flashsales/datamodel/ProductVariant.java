package com.flashsales.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ProductVariant implements Parcelable {
    private String variantParent;
    private ArrayList<Stock> variantValues;
    private boolean isSet;
    private String setValue;

    public ProductVariant(String variantParent) {
        this.variantParent = variantParent;
    }

    public ProductVariant(String variantParent, ArrayList<Stock> variantChildren) {
        this.variantParent = variantParent;
        this.variantValues = variantChildren;
        this.isSet = false;
    }

    public ProductVariant(){}

    public String getVariantParent() {
        return variantParent;
    }

    public void setVariantParent(String variantParent) {
        this.variantParent = variantParent;
    }

    public ArrayList<Stock> getVariantValues() {
        return variantValues;
    }

    public void setVariantValues(ArrayList<Stock> variantValues) {
        this.variantValues = variantValues;
    }

    public String getSetValue() {
        return setValue;
    }

    public void setSetValue(String setValue) {
        this.setValue = setValue;
        if (!isSet)
            isSet = !isSet;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public ProductVariant createFromParcel(Parcel in) {
            return new ProductVariant(in);
        }

        public ProductVariant[] newArray(int size) {
            return new ProductVariant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.variantParent);
        dest.writeList(this.variantValues);
        dest.writeByte((byte) (this.isSet ? 1 : 0));
        dest.writeString(this.setValue);
    }

    public ProductVariant(Parcel in) {
        this.variantParent = in.readString();
        this.variantValues = in.readArrayList(Stock.class.getClassLoader());
        isSet = in.readByte() != 0;
        setValue =  in.readString();
    }
}
