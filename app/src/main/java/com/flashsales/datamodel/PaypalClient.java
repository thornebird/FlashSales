package com.flashsales.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PaypalClient implements Parcelable{
    @SerializedName("environment")
    private String environment;
    @SerializedName("paypal_sdk_version")
    private String sdkVersion;
    @SerializedName("platform")
    private String platform;
   @SerializedName("product_name")
    private String productName;


    public PaypalClient() {
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public static Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public PaypalClient createFromParcel(Parcel in) {
            return new PaypalClient(in);
        }

        public PaypalClient[] newArray(int size) {
            return new PaypalClient[size];
        }
    };

    public PaypalClient(Parcel source){
        this.environment =source.readString();
        this.platform =source.readString();
        this.productName = source.readString();
        this.sdkVersion = source.readString();

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
              dest.writeString(this.environment);
              dest.writeString(this.platform);
              dest.writeString(this.productName);
              dest.writeString(this.sdkVersion);
    }
}
