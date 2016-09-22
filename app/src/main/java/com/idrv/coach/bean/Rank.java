package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time:2016/6/23
 * description:教练排行榜信息
 *
 * @author sunjianfei
 */
public class Rank implements Parcelable {
    String id;
    String phone;
    String nickname;
    String headimgurl;
    String drivingSchool;
    String testSite;
    String trainingSite;
    int coachYears;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getDrivingSchool() {
        return drivingSchool;
    }

    public void setDrivingSchool(String drivingSchool) {
        this.drivingSchool = drivingSchool;
    }

    public String getTestSite() {
        return testSite;
    }

    public void setTestSite(String testSite) {
        this.testSite = testSite;
    }

    public String getTrainingSite() {
        return trainingSite;
    }

    public void setTrainingSite(String trainingSite) {
        this.trainingSite = trainingSite;
    }

    public int getCoachYears() {
        return coachYears;
    }

    public void setCoachYears(int coachYears) {
        this.coachYears = coachYears;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.phone);
        dest.writeString(this.nickname);
        dest.writeString(this.headimgurl);
        dest.writeString(this.drivingSchool);
        dest.writeString(this.testSite);
        dest.writeString(this.trainingSite);
        dest.writeInt(this.coachYears);
    }

    public Rank() {
    }

    protected Rank(Parcel in) {
        this.id = in.readString();
        this.phone = in.readString();
        this.nickname = in.readString();
        this.headimgurl = in.readString();
        this.drivingSchool = in.readString();
        this.testSite = in.readString();
        this.trainingSite = in.readString();
        this.coachYears = in.readInt();
    }

    public static final Parcelable.Creator<Rank> CREATOR = new Parcelable.Creator<Rank>() {
        @Override
        public Rank createFromParcel(Parcel source) {
            return new Rank(source);
        }

        @Override
        public Rank[] newArray(int size) {
            return new Rank[size];
        }
    };
}
