package com.example.shambhavipunja.weatherservice;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by shambhavipunja on 1/12/16.
 */
public class Info implements Parcelable {

    private String temp;
    private String city;
    private Date timestamp;


    public Info() {
        ;
    }

    public Info(Parcel in) {
        readFromParcel(in);
    }

    /*public Info(String temp, String city)
    {
        this.temp=temp;
        this.city=city;
    }*/

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(temp);
        dest.writeString(city);
        dest.writeSerializable(timestamp);

    }

    private void readFromParcel(Parcel in) {
        //in.readParcelable(Info.class.getClassLoader());
        temp = in.readString();
        city = in.readString();
        timestamp = (Date)in.readSerializable();

    }

    public static final Parcelable.Creator<Info> CREATOR
            = new Parcelable.Creator<Info>() {
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };
}
