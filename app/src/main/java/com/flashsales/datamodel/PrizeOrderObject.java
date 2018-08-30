package com.flashsales.datamodel;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class PrizeOrderObject implements Parcelable {
    private Product freeProduct;
    private PaymentObject paymentObject;
    private User user;
    private ShippingAddress address;
    private String emailIdentifier;

    public PrizeOrderObject() {
    }

    public PrizeOrderObject(Product freeProduct, PaymentObject paymentObject, User user, ShippingAddress address, String emailIdentifier) {
        this.freeProduct = freeProduct;
        this.paymentObject = paymentObject;
        this.user = user;
        this.address = address;
        this.emailIdentifier = emailIdentifier;
    }

    public Product getFreeProduct() {
        return freeProduct;
    }

    public void setFreeProduct(Product freeProduct) {
        this.freeProduct = freeProduct;
    }

    public PaymentObject getPaymentObject() {
        return paymentObject;
    }

    public void setPaymentObject(PaymentObject paymentObject) {
        this.paymentObject = paymentObject;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ShippingAddress getAddress() {
        return address;
    }

    public void setAddress(ShippingAddress address) {
        this.address = address;
    }

    public String getEmailIdentifier() {
        return emailIdentifier;
    }

    public void setEmailIdentifier(String emailIdentifier) {
        this.emailIdentifier = emailIdentifier;
    }

    public static Parcelable.Creator CREATOR = new Parcelable.Creator() {
        /**
         * Create a new instance of the Parcelable class, instantiating it
         * from the given Parcel whose data had previously been written by
         * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
         *
         * @param source The Parcel to read the object's data from.
         * @return Returns a new instance of the Parcelable class.
         */
        @Override
        public PrizeOrderObject createFromParcel(Parcel source) {
            return new PrizeOrderObject(source);
        }

        /**
         * Create a new array of the Parcelable class.
         *
         * @param size Size of the array.
         * @return Returns an array of the Parcelable class, with every entry
         * initialized to null.
         */
        @Override
        public PrizeOrderObject[] newArray(int size) {
            return new PrizeOrderObject[size];
        }
    };

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
        dest.writeParcelable(freeProduct, flags);
        dest.writeParcelable(paymentObject, flags);
        dest.writeParcelable(user, flags);
        dest.writeParcelable(address, flags);
        dest.writeString(emailIdentifier);
    }

    public PrizeOrderObject(Parcel in) {
        freeProduct = (Product) in.readParcelable(Product.class.getClassLoader());
        paymentObject = (PaymentObject) in.readParcelable(PaymentObject.class.getClassLoader());
        user = (User) in.readParcelable(User.class.getClassLoader());
        address = (ShippingAddress) in.readParcelable(ShippingAddress.class.getClassLoader());
        emailIdentifier = in.readString();
    }
}
