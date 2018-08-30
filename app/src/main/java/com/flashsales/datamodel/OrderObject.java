package com.flashsales.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.flashsales.Utils.Utils;

public class OrderObject implements Parcelable {
    private String emailIdentifier;
    private PaymentObject paymentObject;
    private CartProduct cartProduct;
    private User user;
    private ShippingAddress shippingAddress;

    public OrderObject(){}

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

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getEmailIdentifier() {
        return emailIdentifier;
    }

    public void setEmailIdentifier(String emailIdentifier) {
        this.emailIdentifier = emailIdentifier;
    }

    public CartProduct getCartProduct() {
        return cartProduct;
    }

    public void setCartProduct(CartProduct cartProduct) {
        this.cartProduct = cartProduct;
    }

    @Override
    public String toString() {
        return "OrderObject{" +
                "emailIdentifier='" + emailIdentifier + '\'' +
                ", paymentObject=" + paymentObject +
                ", cartProduct=" + cartProduct +
                ", user=" + user +
                ", shippingAddress=" + shippingAddress +
                '}';
    }

    public static Parcelable.Creator CREATOR = new Parcelable.Creator(){
        /**
         * Create a new instance of the Parcelable class, instantiating it
         * from the given Parcel whose data had previously been written by
         * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
         *
         * @param source The Parcel to read the object's data from.
         * @return Returns a new instance of the Parcelable class.
         */
        @Override
        public OrderObject createFromParcel(Parcel source) {
            return new OrderObject(source);
        }

        /**
         * Create a new array of the Parcelable class.
         *
         * @param size Size of the array.
         * @return Returns an array of the Parcelable class, with every entry
         * initialized to null.
         */
        @Override
        public OrderObject[] newArray(int size) {
            return new OrderObject[size];
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
        dest.writeString(emailIdentifier);
        dest.writeParcelable(user,flags);
        dest.writeParcelable(paymentObject,flags);
        dest.writeParcelable(cartProduct,flags);
        dest.writeParcelable(shippingAddress,flags);

    }

    public OrderObject(Parcel in){
        emailIdentifier = in.readString();
        user = (User)in.readParcelable(User.class.getClassLoader());
        paymentObject = (PaymentObject) in.readParcelable(PaymentObject.class.getClassLoader());
        cartProduct = (CartProduct)in.readParcelable(CartProduct.class.getClassLoader());
        shippingAddress = (ShippingAddress)in.readParcelable(ShippingAddress.class.getClassLoader());

    }
}
