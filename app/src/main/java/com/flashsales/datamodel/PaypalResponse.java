package com.flashsales.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PaypalResponse implements Parcelable{
    @SerializedName("create_time")
    private String createdTime;
    @SerializedName("id")
    private String id;
    @SerializedName("intent")
    private String intent;
    @SerializedName("state")
    private String state;

    public PaypalResponse() {
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public PaypalResponse createFromParcel(Parcel in) {
            return new PaypalResponse(in);
        }

        public PaypalResponse[] newArray(int size) {
            return new PaypalResponse[size];
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
        dest.writeString(this.createdTime);
        dest.writeString(this.id);
        dest.writeString(this.intent);
        dest.writeString(this.state);
    }
    public PaypalResponse(Parcel in) {
        this.createdTime = in.readString();
        this.id = in.readString();
        this.intent = in.readString();
        this.state = in.readString();
    }



}
