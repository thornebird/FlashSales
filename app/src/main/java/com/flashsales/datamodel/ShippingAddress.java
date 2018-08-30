package com.flashsales.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class ShippingAddress implements Parcelable {
    private String telephone;
    private String shippingAddress;
    private String apt;
    private String city;
    private String province;
    private String zipCode;
    private String country;

    public ShippingAddress() {
    }

    public ShippingAddress(String telephone, String shippingAddress, String apt, String city, String province, String zipCode, String country) {
        this.telephone = telephone;
        this.shippingAddress = shippingAddress;
        this.apt = apt;
        this.city = city;
        this.province = province;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getApt() {
        return apt;
    }

    public void setApt(String apt) {
        this.apt = apt;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return apt+" ," +shippingAddress +" ," +city +" ," + province +" ," + zipCode +" ," + country ;
    }

    public ShippingAddress(Parcel in) {
        this.telephone = in.readString();
        this.shippingAddress = in.readString();
        this.apt = in.readString();
        this.city = in.readString();
        this.province = in.readString();
        this.country = in.readString();
        this.zipCode = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public ShippingAddress createFromParcel(Parcel in) {
            return new ShippingAddress(in);
        }

        public ShippingAddress[] newArray(int size) {
            return new ShippingAddress[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.telephone);
        dest.writeString(this.shippingAddress);
        dest.writeString(this.apt);
        dest.writeString(this.city);
        dest.writeString(this.province);
        dest.writeString(this.country);
        dest.writeString(this.zipCode);
    }
}
