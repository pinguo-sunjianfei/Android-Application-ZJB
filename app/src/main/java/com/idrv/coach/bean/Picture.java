package com.idrv.coach.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time:2016/4/22
 * description:网络图片
 *
 * @author sunjianfei
 */
public class Picture implements Parcelable {
    private String id;
    private String url;
    private String created;
    private String eTag;
    private boolean isFake;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public boolean isFake() {
        return isFake;
    }

    public void setIsFake(boolean isFake) {
        this.isFake = isFake;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.url);
        dest.writeString(this.created);
        dest.writeString(this.eTag);
        dest.writeByte(isFake ? (byte) 1 : (byte) 0);
    }

    public Picture() {
    }

    protected Picture(Parcel in) {
        this.id = in.readString();
        this.url = in.readString();
        this.created = in.readString();
        this.eTag = in.readString();
        this.isFake = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
        public Picture createFromParcel(Parcel source) {
            return new Picture(source);
        }

        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

    @Override
    public int hashCode() {
        return url.hashCode() * 31;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Picture picture = (Picture) o;
        if (url.equals(picture.url)) return true;
        return false;
    }
}
