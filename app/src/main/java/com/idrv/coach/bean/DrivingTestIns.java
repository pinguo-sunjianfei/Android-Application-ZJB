package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time:2016/3/29
 * description:
 *
 * @author sunjianfei
 */
public class DrivingTestIns implements Parcelable {
    String name;
    String idCard;
    String coachPhone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getCoachPhone() {
        return coachPhone;
    }

    public void setCoachPhone(String coachPhone) {
        this.coachPhone = coachPhone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.idCard);
        dest.writeString(this.coachPhone);
    }

    public DrivingTestIns() {
    }

    protected DrivingTestIns(Parcel in) {
        this.name = in.readString();
        this.idCard = in.readString();
        this.coachPhone = in.readString();
    }

    public static final Parcelable.Creator<DrivingTestIns> CREATOR = new Parcelable.Creator<DrivingTestIns>() {
        public DrivingTestIns createFromParcel(Parcel source) {
            return new DrivingTestIns(source);
        }

        public DrivingTestIns[] newArray(int size) {
            return new DrivingTestIns[size];
        }
    };
}
