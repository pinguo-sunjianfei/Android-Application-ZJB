package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time:15-9-25
 * description:城市实体
 *
 * @author sunjianfei
 */

public class City implements Parcelable {
    private String province = "";
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFristPY;

    public City() {
    }

    public City(String province, String city, String number, String firstPY,
                String allPY, String allFristPY) {
        super();
        this.province = province;
        this.city = city;
        this.number = number;
        this.firstPY = firstPY;
        this.allPY = allPY;
        this.allFristPY = allFristPY;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFirstPY() {
        return firstPY;
    }

    public void setFirstPY(String firstPY) {
        this.firstPY = firstPY;
    }

    public String getAllPY() {
        return allPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public String getAllFristPY() {
        return allFristPY;
    }

    public void setAllFristPY(String allFristPY) {
        this.allFristPY = allFristPY;
    }

    @Override
    public String toString() {
        return "City [province=" + province + ", city=" + city + ", number="
                + number + ", firstPY=" + firstPY + ", allPY=" + allPY
                + ", allFristPY=" + allFristPY + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.number);
        dest.writeString(this.firstPY);
        dest.writeString(this.allPY);
        dest.writeString(this.allFristPY);
    }

    protected City(Parcel in) {
        this.province = in.readString();
        this.city = in.readString();
        this.number = in.readString();
        this.firstPY = in.readString();
        this.allPY = in.readString();
        this.allFristPY = in.readString();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        public City createFromParcel(Parcel source) {
            return new City(source);
        }

        public City[] newArray(int size) {
            return new City[size];
        }
    };
}
