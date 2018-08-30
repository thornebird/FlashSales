package com.flashsales.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PaymentObject implements Parcelable {
    @SerializedName("response_type")
    private String responseType;
    private PaypalClient client;
    private PaypalResponse paypalResponse;

    public PaymentObject() {
    }

    public PaypalClient getClient() {
        return client;
    }

    public void setClient(PaypalClient client) {
        this.client = client;
    }

    public PaypalResponse getPaypalResponse() {
        return paypalResponse;
    }

    public void setPaypalResponse(PaypalResponse paypalResponse) {
        this.paypalResponse = paypalResponse;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
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
        public PaymentObject createFromParcel(Parcel source) {
            return new PaymentObject(source);
        }

        /**
         * Create a new array of the Parcelable class.
         *
         * @param size Size of the array.
         * @return Returns an array of the Parcelable class, with every entry
         * initialized to null.
         */
        @Override
        public PaymentObject[] newArray(int size) {
            return new PaymentObject[size];
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
        dest.writeParcelable(client, flags);
        dest.writeParcelable(paypalResponse, flags);
        dest.writeString(responseType);
    }

    public PaymentObject(Parcel in) {
        client = (PaypalClient) in.readParcelable(PaypalClient.class.getClassLoader());
        paypalResponse = (PaypalResponse) in.readParcelable(PaypalResponse.class.getClassLoader());
        responseType = in.readString();
    }

}
