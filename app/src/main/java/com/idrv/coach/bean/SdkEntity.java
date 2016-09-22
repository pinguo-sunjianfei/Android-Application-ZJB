package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sunjianfei on 2016/3/7.
 * description: 封装了请求发出时的最基本参数
 */
public class SdkEntity implements Parcelable {
    //公有字段
    String platform;
    String uid;
    String token;
    String cid;
    String device;
    //纬度
    String longtitude;
    //经度
    String latitude;
    //版本号
    String appVersion;
    //系统版本号
    String systemVersion;
    //app名称
    String appName;
    //机型
    String build;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public SdkEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.platform);
        dest.writeString(this.uid);
        dest.writeString(this.token);
        dest.writeString(this.cid);
        dest.writeString(this.device);
        dest.writeString(this.longtitude);
        dest.writeString(this.latitude);
        dest.writeString(this.appVersion);
        dest.writeString(this.systemVersion);
        dest.writeString(this.appName);
        dest.writeString(this.build);
    }

    protected SdkEntity(Parcel in) {
        this.platform = in.readString();
        this.uid = in.readString();
        this.token = in.readString();
        this.cid = in.readString();
        this.device = in.readString();
        this.longtitude = in.readString();
        this.latitude = in.readString();
        this.appVersion = in.readString();
        this.systemVersion = in.readString();
        this.appName = in.readString();
        this.build = in.readString();
    }

    public static final Creator<SdkEntity> CREATOR = new Creator<SdkEntity>() {
        public SdkEntity createFromParcel(Parcel source) {
            return new SdkEntity(source);
        }

        public SdkEntity[] newArray(int size) {
            return new SdkEntity[size];
        }
    };
}
