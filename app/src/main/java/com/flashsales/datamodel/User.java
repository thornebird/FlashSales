package com.flashsales.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{
    private User user;
    private String name;
    private String email;
    private String gender;
    private String password;
    private String imageFb;
   /* public ShippingAddress shippingAddress;*/


    public User(){}



    /*public User(String firstName,String lastName,String email,String gender){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
/* //public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }*/

    /*public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }*/

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getImageFb() {
        return imageFb;
    }

    public void setImageFb(String imageFb) {
        this.imageFb = imageFb;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public User createFromParcel(Parcel in){
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.gender);
        dest.writeParcelable(user,flags);
        dest.writeString(this.password);
    }

    public User(Parcel in){
        this.name = in.readString();
        this.email = in.readString();
        this.gender = in.readString();
        this.password = in.readString();
        this.user= (User)in.readParcelable(User.class.getClassLoader());
    }
}
