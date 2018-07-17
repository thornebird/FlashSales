package com.flashsales.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable{
    private String reviewerName;
    private String review;
    private double rating;
    private int imageId;

    public Review(){}

    public Review(String name,String review,double rating,int imageId){
        this.reviewerName =name;
        this.review = review;
        this.rating =rating;
        this.imageId =imageId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        public Review createFromParcel(Parcel in){
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public Review(Parcel in){
        this.reviewerName = in.readString();
        this.review = in.readString();
        this.rating = in.readDouble();
        this.imageId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.review);
        dest.writeString(this.reviewerName);
        dest.writeDouble(this.rating);
        dest.writeInt(this.imageId);
    }
}
