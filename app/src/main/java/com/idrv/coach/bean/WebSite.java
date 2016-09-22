package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * time:2016/6/12
 * description:个人网站
 *
 * @author sunjianfei
 */
public class WebSite implements Parcelable {
    String uid;
    String headimgurl;
    String nickname;
    String updated;
    String drivingSchool;
    String coachingDate;
    List<Picture> pictures;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getDrivingSchool() {
        return drivingSchool;
    }

    public void setDrivingSchool(String drivingSchool) {
        this.drivingSchool = drivingSchool;
    }

    public String getCoachingDate() {
        return coachingDate;
    }

    public void setCoachingDate(String coachingDate) {
        this.coachingDate = coachingDate;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.headimgurl);
        dest.writeString(this.nickname);
        dest.writeString(this.updated);
        dest.writeString(this.drivingSchool);
        dest.writeString(this.coachingDate);
        dest.writeTypedList(pictures);
    }

    public WebSite() {
    }

    protected WebSite(Parcel in) {
        this.uid = in.readString();
        this.headimgurl = in.readString();
        this.nickname = in.readString();
        this.updated = in.readString();
        this.drivingSchool = in.readString();
        this.coachingDate = in.readString();
        this.pictures = in.createTypedArrayList(Picture.CREATOR);
    }

    public static final Parcelable.Creator<WebSite> CREATOR = new Parcelable.Creator<WebSite>() {
        public WebSite createFromParcel(Parcel source) {
            return new WebSite(source);
        }

        public WebSite[] newArray(int size) {
            return new WebSite[size];
        }
    };
}
